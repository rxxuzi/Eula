package eula;

import global.Config;
import global.Log;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EulaCollections {
    public static List<File> getFileList(File dir) {
        List <File> list = new ArrayList<>();
        if(dir.isDirectory()){
            EulaManager em = new EulaManager(dir);
            list = em.getFileList();
        }else{
            list.add(dir);
        }
        return list;
    }

    public static String removeExtension(String path, String extension) {
        if (path.endsWith(extension)) {
            return path.substring(0, path.length() - extension.length());
        }
        return path;
    }

    public static String formatTime(long millis) {
        long hours = millis / (3600 * 1000);
        long minutes = (millis % (3600 * 1000)) / (60 * 1000);
        long seconds = (millis % (60 * 1000)) / 1000;
        long milliseconds = millis % 1000;

        return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, milliseconds);
    }

    public static boolean isIgnoredFile(File file) {
        for (String ignoredFile : Config.IGNORE_EXTENSION) {
            if (file.getName().endsWith(ignoredFile)) {
                return true;
            }
        }
        String path = file.getAbsolutePath();
        return path.endsWith(Config.CONFIG_FILE);
    }

    public static void openDir(String dir){
        try {
            // ディレクトリのパスを指定
            File directory = new File(dir);

            // デスクトップサポートがあるかチェック
            if (!Desktop.isDesktopSupported()) {
                Log.warn("Desktop is not supported");
                return;
            }

            Desktop desktop = Desktop.getDesktop();
            if (directory.exists()) {
                // ディレクトリを開く
                desktop.open(directory);
            } else {
                Log.warn("Directory does not exist");
            }
        } catch (IOException e) {
            Log.err(e);
        }
    }
}
