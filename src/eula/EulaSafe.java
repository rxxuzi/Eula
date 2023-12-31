package eula;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.List;

public final class EulaSafe {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final int KEY_SIZE = 256;
    private static final byte[] SALT = "Fast Eula by rxxuzi".getBytes();
    private static final String EXTENSION = ".eulax";
    private static final int BUFFER_SIZE = 8192 * 2;

    private static void encrypt(Key key, File inputFile, boolean delete) throws EulaException, IOException {
        String filename = inputFile.getAbsolutePath() + EXTENSION;
        File outputFile = new File(filename);
        File compressedFile = new File(inputFile.getName() + EXTENSION);
        compressFile(inputFile, compressedFile);
        doCrypto(Cipher.ENCRYPT_MODE, key, compressedFile, outputFile);
        compressedFile.delete();

        if (delete) inputFile.delete();
    }

    private static void decrypt(Key key, File inputFile, boolean delete) throws EulaException, IOException {
        if (inputFile.getPath().endsWith(EXTENSION)) {
            String outputFilePath = EulaCollections.removeExtension(inputFile.getAbsolutePath(), EXTENSION);
            File outputFile = new File(outputFilePath);
            File decompressedFile = new File(outputFile.getName() + EXTENSION);
            doCrypto(Cipher.DECRYPT_MODE, key, inputFile, decompressedFile);
            decompressFile(decompressedFile, outputFile);
            decompressedFile.delete();

            if (delete) inputFile.delete();
        }
    }

    public static void encrypt(String password, List<File> files, boolean delete) throws EulaException {
        Key key = generateKey(password);
        processFiles(key, files, delete, true);
    }

    public static void decrypt(String password, List<File> files, boolean delete) throws EulaException {
        Key key = generateKey(password);
        processFiles(key, files, delete, false);
    }

    private static void processFiles(Key key, List<File> files, boolean delete, boolean isEncrypt) {
        files.parallelStream().forEach(file -> {
            try {
                if (!isEncrypt && !file.getPath().endsWith(EXTENSION)) {
                    return;
                }
                if (isEncrypt) {
                    encrypt(key, file, delete);
                } else {
                    decrypt(key, file, delete);
                }
            } catch (IOException | EulaException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void encrypt(String password, String  inputFile, boolean delete) throws EulaException {
        encrypt(password, EulaCollections.getFileList(new File(inputFile)), delete);
    }

    public static void decrypt(String password, String inputFile, boolean delete) throws EulaException {
        decrypt(password, EulaCollections.getFileList(new File(inputFile)), delete);
    }

    private static Key generateKey(String password) throws EulaException {
        try {
            return getKeyFromPassword(password);
        } catch (NoSuchAlgorithmException e) {
            throw new EulaException("No such algorithm for key generation", e);
        } catch (InvalidKeySpecException e) {
            throw new EulaException("Invalid key specification for key generation", e);
        }
    }

    private static Key getKeyFromPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), SALT, 65536, KEY_SIZE);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITHM);
    }

    private static void doCrypto(int cipherMode, Key secretKey, File inputFile, File outputFile) throws EulaException {
        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);
            outputStream.write(outputBytes);

        } catch (NoSuchPaddingException e) {
            throw new EulaException("Padding problem in encryption/decryption", e);
        } catch (NoSuchAlgorithmException e) {
            throw new EulaException("Algorithm not found in encryption/decryption", e);
        } catch (InvalidKeyException e) {
            throw new EulaException("Invalid key in encryption/decryption", e);
        } catch (BadPaddingException e) {
            throw new EulaException("Bad padding in encryption/decryption", e);
        } catch (IllegalBlockSizeException e) {
            throw new EulaException("Illegal block size in encryption/decryption", e);
        } catch (IOException e) {
            throw new EulaException("I/O error in encryption/decryption", e);
        }
    }

    private static void compressFile(File inputFile, File outputFile) throws IOException {
        try (LZ4BlockOutputStream lz4OutStream = new LZ4BlockOutputStream(new FileOutputStream(outputFile));
             FileInputStream fis = new FileInputStream(inputFile)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                lz4OutStream.write(buffer, 0, length);
            }
        }
    }

    private static void decompressFile(File inputFile, File outputFile) throws IOException {
        try (LZ4BlockInputStream lz4InStream = new LZ4BlockInputStream(new FileInputStream(inputFile));
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = lz4InStream.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }
    }
}
