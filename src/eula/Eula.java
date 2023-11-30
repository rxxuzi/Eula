package eula;

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
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Eula {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final int KEY_SIZE = 128;
    private static final byte[] SALT = "EuLA by rxxuzi".getBytes();
    private static final String EXTENSION = ".eula";

    // 暗号化メソッドの変更
    public static void encrypt(String key, File inputFile, File outputFile) throws CryptoException, IOException {
        File compressedFile = new File(inputFile.getName() + EXTENSION);
        compressFile(inputFile, compressedFile);
        doCrypto(Cipher.ENCRYPT_MODE, key, compressedFile, outputFile);
        compressedFile.delete();
    }

    public static void encrypt(String key, File inputFile, boolean delete) throws IOException, CryptoException {
        String filename = inputFile.getAbsolutePath() + EXTENSION;
        File outputFile = new File(filename);
        if (outputFile.createNewFile()){
            encrypt(key, inputFile, outputFile);
        }

        if (delete) inputFile.delete();
    }

    public static void encrypt(String key, List<File> files, boolean delete) throws InterruptedException, ExecutionException {
        files.parallelStream().forEach(file -> {
            try {
                encrypt(key, file, delete);
            } catch (IOException | CryptoException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // 複合化メソッドの変更
    public static void decrypt(String key, File inputFile, File outputFile) throws CryptoException, IOException {
        if (inputFile.getPath().endsWith(EXTENSION)) {
            File decompressedFile = new File(outputFile.getName() + EXTENSION);
            doCrypto(Cipher.DECRYPT_MODE, key, inputFile, decompressedFile);
            decompressFile(decompressedFile, outputFile);
            decompressedFile.delete();
        }
    }

    public static void decrypt(String key, File inputFile, boolean delete) throws CryptoException, IOException {
        if (inputFile.getPath().endsWith(EXTENSION)) {
            String outputFilePath = removeExtension(inputFile.getAbsolutePath());
            File outputFile = new File(outputFilePath);
            File decompressedFile = new File(outputFile.getName() + EXTENSION);
            doCrypto(Cipher.DECRYPT_MODE, key, inputFile, decompressedFile);
            decompressFile(decompressedFile, outputFile);
            decompressedFile.delete();

            if (delete) inputFile.delete();
        }
    }

    public static void decrypt(String key, List<File> files, boolean delete) {
        files.parallelStream().forEach(file -> {
            try {
                if (file.getPath().endsWith(EXTENSION)){
                    decrypt(key, file, delete);
                }
            } catch (IOException | CryptoException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile) throws CryptoException {
        try {
            Key secretKey = getKeyFromPassword(key, SALT);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException |
                 IllegalBlockSizeException | IOException | InvalidKeySpecException e) {
            throw new CryptoException("Error encrypting/decrypting file", e);
        }
    }

    private static Key getKeyFromPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, KEY_SIZE);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITHM);
    }

    // 圧縮メソッド
    public static void compressFile(File inputFile, File outputFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFile))) {
            zos.putNextEntry(new ZipEntry(inputFile.getName()));
            FileInputStream fis = new FileInputStream(inputFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            fis.close();
            zos.closeEntry();
        }
    }

    // 解凍メソッド
    public static void decompressFile(File inputFile, File outputFile) throws IOException {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(inputFile))) {
            ZipEntry zipEntry = zis.getNextEntry();
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
            }
            zis.closeEntry();
        }
    }

    private static String removeExtension(String path) {
        if (path.endsWith(EXTENSION)) {
            return path.substring(0, path.length() - EXTENSION.length());
        }
        return path;
    }

    public static class CryptoException extends Exception {
        public CryptoException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }

}
