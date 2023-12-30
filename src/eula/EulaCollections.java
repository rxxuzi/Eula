package eula;

import java.io.File;
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
}
