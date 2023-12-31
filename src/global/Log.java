package global;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    // ログレベルの定義
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARN = 3;
    public static final int ERROR = 4;

    private static int currentLogLevel = DEBUG;

    public static void out(int level, String message) {
        if (level >= currentLogLevel) {
            String logMessage = formatLogMessage(level, message);
            writeLog(logMessage);
        }
    }

    //　エラーログ
    public static void err(int level, Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        out(level, e.getMessage() + "\nStackTrace:\n" + exceptionAsString);
        System.out.println(e.getMessage() + "\nStackTrace:\n" + exceptionAsString);
    }

    public static void err(Exception e) {
        err(ERROR,e);
    }
    
    // ログメッセージのフォーマット
    private static String formatLogMessage(int level, String message) {
        String logLevelString = switch (level) {
            case DEBUG -> "DEBUG";
            case INFO -> "INFO";
            case WARN -> "WARN";
            case ERROR -> "ERROR";
            default -> "UNKNOWN";
        };
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return String.format(Config.LOG_FORMAT, dateFormat.format(new Date()), logLevelString, message);
    }

    // ログファイルへの書き込み
    private static void writeLog(String logMessage) {
        try (FileWriter writer = new FileWriter(Config.LOG_DIR + "app-"+Config.DATE+".log", true)) {
            writer.write(logMessage + "\n");
            System.out.println("save at : " + Config.LOG_DIR + "app-"+Config.DATE+".log");
        } catch (IOException e) {
            System.err.println("Failed to write out: " + e.getMessage());
        }
    }

    // ログレベルの設定
    public static void setLogLevel(int logLevel) {
        currentLogLevel = logLevel;
    }
}
