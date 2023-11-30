package eula;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EulaChecker {

    public static boolean isSameFile(String filePathA , String filePathB) throws NoSuchAlgorithmException, IOException {
        String hashA = calculateFileHash(filePathA);
        String hashB = calculateFileHash(filePathB);

        return hashA.equals(hashB);
    }

    public static boolean isSameFile(File fileA , File fileB) throws NoSuchAlgorithmException, IOException {
        String hashA = calculateFileHash(fileA.getPath());
        String hashB = calculateFileHash(fileB.getPath());

        return hashA.equals(hashB);
    }

    private static String calculateFileHash(String filePath) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        FileInputStream fis = new FileInputStream(filePath);

        byte[] byteArray = new byte[1024];
        int bytesCount;

        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        fis.close();

        byte[] bytes = digest.digest();

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


}
