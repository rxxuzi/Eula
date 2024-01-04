package eula;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import eula.EulaManager;
import global.Config;
import global.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
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

    public static List<String> getListFromJson(String path) {
        Gson gson = new Gson();
        try {
            FileReader reader = new FileReader(path);
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            return gson.fromJson(reader, listType);
        } catch (FileNotFoundException e) {
            Log.err(e);
        }
        return Collections.emptyList();
    }

    public static boolean isIgnoredFile(File file) {
        for (String ignoredFile : Config.IGNORE_EXTENSION) {
            if (file.getName().endsWith(ignoredFile)) {
                return true;
            }
        }
        return false;
    }
}
