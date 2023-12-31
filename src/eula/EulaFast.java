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
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.List;

public final class EulaFast {
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;
    private static final byte[] SALT = "Fast Eula by rxxuzi".getBytes();
    private static final String EXTENSION = ".eulax";
    private static final int BUFFER_SIZE = 8192;

    // 暗号化メソッド
    private static void encrypt(Key key, File inputFile, boolean delete) throws EulaException, IOException {
        ByteArrayOutputStream compressedOutputStream = new ByteArrayOutputStream();

        try (FileInputStream fis = new FileInputStream(inputFile);
             LZ4BlockOutputStream lz4OutputStream = new LZ4BlockOutputStream(compressedOutputStream);
             CipherOutputStream cos = new CipherOutputStream(lz4OutputStream, getCipher(Cipher.ENCRYPT_MODE, key))) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }
        }

        try (FileOutputStream fos = new FileOutputStream(inputFile.getAbsolutePath() + EXTENSION)) {
            fos.write(compressedOutputStream.toByteArray());
        }

        if (delete) inputFile.delete();
    }


    // 複合化メソッド
    private static void decrypt(Key key, File inputFile, boolean delete) throws EulaException, IOException {
        if (inputFile.getPath().endsWith(EXTENSION)) {
            try (FileInputStream fis = new FileInputStream(inputFile);
                 LZ4BlockInputStream lz4InputStream = new LZ4BlockInputStream(fis);
                 CipherInputStream cis = new CipherInputStream(lz4InputStream, getCipher(Cipher.DECRYPT_MODE, key));
                 FileOutputStream fos = new FileOutputStream(EulaCollections.removeExtension(inputFile.getAbsolutePath(), EXTENSION))) {

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = cis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }

            if (delete) inputFile.delete();
        }
    }

    // Cipherオブジェクトを取得するユーティリティメソッド
    private static Cipher getCipher(int cipherMode, Key secretKey) throws EulaException {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(cipherMode, secretKey);
            return cipher;
        } catch (NoSuchPaddingException e) {
            throw new EulaException("Padding problem in encryption/decryption", e);
        } catch (NoSuchAlgorithmException e) {
            throw new EulaException("Algorithm not found in encryption/decryption", e);
        } catch (InvalidKeyException e) {
            throw new EulaException("Invalid key in encryption/decryption", e);
        }
    }

    public static void encrypt(String password, List<File> files, boolean delete) throws EulaException {
        Key key = generateKey(password);
        files.parallelStream().forEach(file -> {
            try {
                encrypt(key, file, delete);
            } catch (IOException | EulaException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void decrypt(String password, List<File> files, boolean delete) throws EulaException {
        Key key = generateKey(password);
        files.parallelStream().forEach(file -> {
            try {
                if (file.getPath().endsWith(EXTENSION)){
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
}
