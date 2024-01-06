package etc;

import eula.EulaManager;

import java.io.File;

public class Del {
    public static void main(String[] args) {
        var dir = new File("./res");
        var em = new EulaManager(dir);
        em.removeAllFiles();
    }
}
