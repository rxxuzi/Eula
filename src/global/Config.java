package global;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Config {

    public static final Fast fast = loadConfig();
    public static final String CONFIG_FILE = "config/config.json";

    public static Fast loadConfig() {
        try {
            String json = new String(Files.readAllBytes(Paths.get(CONFIG_FILE)));
            Gson gson = new Gson();
            return gson.fromJson(json, Fast.class);
        } catch (IOException ignored) {
            System.out.println("Config file not found.");
            System.exit(1);
            return null;
        }
    }

    public static final String VERSION = "1.0.0 - SNAPSHOT";
    public static boolean DEBUG = false;

    /* パス関連 */
    public static final String LOG_DIR = fast.path.log;
    public static final String CONFIG_DIR = fast.path.config;
    public static final String USR_DIR = fast.path.user;

    /* 暗号化に関する設定 */

    //　暗号化時に無視するファイルの設定
    public static List<String> IGNORE_EXTENSION = fast.encrypt.ignore;

    // 暗号化したファイルの拡張子
    public static final Map<String, String> EXTENSION = fast.encrypt.extension;

    // ソルト
    public static final byte[] SALT = fileToBytes(fast.encrypt.salt);
    public static final String SALT_PATH = fast.encrypt.salt.file_path;

    public static final String ALGORITHM = fast.encrypt.algorithm;
    public static final String TRANSFORMATION = fast.encrypt.transformation;
    public static final int KEY_SIZE = fast.encrypt.key_size;
    public static final int BUFFER_SIZE = fast.encrypt.buffer_size;
    public static final int ITERATION_COUNT = fast.encrypt.iteration_count;

    /* システムリソース関連の設定 */
    public static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    public static final long MAX_MEMORY = Runtime.getRuntime().maxMemory() / 1024 / 1024; // MB

    public static final int MAX_RETRY = 3;

    /* ロギング関連の設定 */
    public static final boolean SAVE = fast.save_log;
    public static final int LOG_FILE_MAX_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String LOG_FORMAT = "[%s] [%s] %s";

    public static final String DATE = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    static {
        mkdir(LOG_DIR);
        mkdir(CONFIG_DIR);
        mkdir(USR_DIR);
    }

    static void mkdir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdir()) {
                Log.info("mkdir " + path);
            }
        }
    }

    private static byte[] fileToBytes(Fast.EncryptConfig.SaltConfig salt) {
        String content;
        if(salt.use_file){
            try {
                content = new String(Files.readAllBytes(Paths.get(salt.file_path)));
            } catch (IOException e) {
                content = salt.default_value;
            }
        }else{
            content = salt.default_value;
        }

        return content.getBytes();
    }
}
