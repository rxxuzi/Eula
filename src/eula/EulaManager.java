package eula;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EulaManager {

    private final List<File> list = new ArrayList<>();

    public EulaManager(File file){
        listFiles(file);
    }

    public EulaManager(String path){
        this(new File(path));
    }

    public void removeFiles(boolean eulaFile){
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).exists()){
                if (eulaFile == list.get(i).getPath().endsWith(".eula")){
                    if(list.get(i).delete()){
                        list.remove(i);
                    }
                }
            }
        }
    }

    public void removeAllFiles(){
        for (File file : list) {
            file.delete();
        }
        list.clear();
    }

    public List<File> getFileList() {
        return list;
    }

    private void listFiles(File dir){
        list.clear();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    listFiles(file);
                } else {
                    list.add(file);
                }
            }
        }
    }
}
