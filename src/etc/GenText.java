package etc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GenText {
    private static final int TEXT_LENGTH = 100000;
    // 以前と同じランダムな文字列を生成するメソッド
    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    // 以前と同じファイルを生成するメソッド
    private void createFileWithRandomText(String folderPath, int fileIndex) throws IOException {
        String fileName =  fileIndex + ".txt";
        File file = new File(folderPath, fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            String randomText = generateRandomString(TEXT_LENGTH);
            writer.write(randomText);
        }
    }

    // 並列処理でランダムなテキストファイルを生成するメソッド
    public long createRandomTextFilesConcurrently(String folderPath, int numberOfFiles) {
        long start = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < numberOfFiles; i++) {
            final int fileIndex = i;
            executor.submit(() -> {
                try {
                    createFileWithRandomText(folderPath, fileIndex);
                } catch (IOException e) {
                    System.err.println("Error creating file: " + e.getMessage());
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long end = System.currentTimeMillis();
        return end - start;
    }

    public static void main(String[] args) {
        GenText genText = new GenText();
        int numF = 500;
        long time = genText.createRandomTextFilesConcurrently("./res", numF);
        System.out.println("Files: " +numF);
        System.out.println("GEN TIME : " + time + " ms");
    }
}

