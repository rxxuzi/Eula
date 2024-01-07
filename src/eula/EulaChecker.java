package eula;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EulaChecker {

    public static boolean isSameFile(String filePathA , String filePathB) throws EulaException {
        String hashA = calculateFileHash(filePathA);
        String hashB = calculateFileHash(filePathB);

        return hashA.equals(hashB);
    }

    public static boolean isSameFile(File fileA , File fileB) throws EulaException {
        String hashA = calculateFileHash(fileA.getPath());
        String hashB = calculateFileHash(fileB.getPath());

        return hashA.equals(hashB);
    }

    public static boolean isSameFile(File file , String hash) throws EulaException {
        String hashA = calculateFileHash(file.getAbsolutePath());

        return hashA.equals(hash);
    }

    public static String calculateFileHash(String filePath) throws EulaException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new EulaException("SHA-256 algorithm not found", e);
        }

        try(FileInputStream fis =  new FileInputStream(filePath)) {
            byte[] byteArray = new byte[1024];
            int bytesCount;

            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        } catch (FileNotFoundException e) {
            throw new EulaException("File not found", e);
        } catch (IOException e) {
            throw new EulaException("I/O error in reading file", e);
        }

        byte[] bytes = digest.digest();

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error computing hash", e);
        }
    }

}
