package global;

import java.util.List;
import java.util.Map;

public class Fast {
    public  boolean save_log;
    public  EncryptConfig encrypt;
    public  SshConfig ssh;
    public  PathConfig path;

    public static class EncryptConfig {
        public Map<String, String> extension;
        public String algorithm;
        public String transformation;
        public int key_size;
        public int buffer_size;
        public int iteration_count;
        public SaltConfig salt;
        public List<String> ignore;

        public static class SaltConfig {
            public boolean use_file;
            public String file_path;
            public String default_value;
        }
    }

    public static class SshConfig {
        public  String host;
        public  int port;
        public  String user;
        public  String password;
    }

    public static class PathConfig {
        public String log;
        public String config;
        public String user;
        public String key;
    }
}
