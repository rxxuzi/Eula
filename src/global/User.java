package global;

import etc.GenSalt;
import eula.EulaChecker;
import eula.EulaException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
    public static final String USER_NAME = System.getProperty("user.name");
    public static final String OS_NAME = System.getProperty("os.name");
    public static final String OS_ARCH = System.getProperty("os.arch");
    public static final String USER_COUNTRY = System.getProperty("user.country") != null ?
            System.getProperty("user.country") :
            System.getProperty("user.region");

    private static final String USER_FILE_PATH = Config.USR_DIR + "user.txt";

    public static void writeUserInfoToFile(String filePath) throws IOException {
        String saltHash;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            saltHash = EulaChecker.calculateFileHash(Config.SALT_PATH);
        } catch (EulaException e) {
            saltHash = "error";
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // タイムスタンプを記録
            writer.write("eula.version=" + Config.VERSION + "\n");
            writer.write("record.timestamp=" + dateFormat.format(new Date()) + "\n");
            writer.write("user.name=" + User.USER_NAME + "\n");
            writer.write("user.os=" + User.OS_NAME + "\n");
            writer.write("user.arch=" + User.OS_ARCH + "\n");
            writer.write("user.country=" + User.USER_COUNTRY + "\n");
            writer.write("user.file.path=" + User.USER_FILE_PATH + "\n");
            writer.write("salt.path=" + Config.SALT_PATH + "\n");
            writer.write("salt.hash=" + saltHash + "\n");
        }
    }

    private static String extractHash() {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("salt.hash=")) {
                    return line.split("=")[1];
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        return null;
    }
    public static void write(){
        try{
            if(!new File(Config.SALT_PATH).exists()) GenSalt.getSalt();
            writeUserInfoToFile(USER_FILE_PATH);
        } catch (EulaException | IOException e) {
            Log.err(e);
        }
    }

    public static boolean isSaltSame() throws EulaException {
        return EulaChecker.isSameFile(new File(Config.SALT_PATH) , extractHash());
    }
}
