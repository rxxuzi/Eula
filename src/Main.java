import eula.Eula;
import eula.EulaManager;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String key = "password";
        File dir = new File("./res");
        EulaManager em = new EulaManager(dir);
        List<File> list = em.getFileList();
        long start = System.currentTimeMillis();
        Eula.encrypt(key, list, true);
//        Eula.decrypt(key,list,true);
        System.out.println(System.currentTimeMillis() - start + "ms");
    }
}

