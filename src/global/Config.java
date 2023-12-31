package global;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Config {
    //パス関連
    public static final String USER_PATH = "./config/user/user.json";
    public static final String SSH_PATH = "./config/key/ssh.json";
    public static final String LOG_DIR = "./log/";
    public static final String CONFIG_DIR = "./config/";
    public static final String CONFIG_FILE = "./config/config.json";
    public static boolean DEBUG = false;

    // システムリソース関連の設定
    public static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    public static final long MAX_MEMORY = Runtime.getRuntime().maxMemory() / 1024 / 1024; // MB

    public static final int MAX_RETRY = 3;

    // ロギング関連の設定
    public static final int LOG_FILE_MAX_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String LOG_FORMAT = "[%s] [%s] %s";

    public static final String DATE = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    static {
        mkdir(LOG_DIR);
        mkdir(CONFIG_DIR);
    }

    static void mkdir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
