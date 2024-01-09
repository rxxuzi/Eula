package main;

import eula.*;
import eula.EulaCollections;
import global.Config;
import global.Log;
import global.User;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) {
        if(args.length == 0){
            System.out.println("Eula "+ Config.VERSION);
            System.out.println("  -h, --help         : Show this help.");
            System.out.println("  -v, --version      : Show version.");
            System.out.println("  -l, --log          : Open log directory.");
            System.out.println("  -g, --generate     : Generate Salt and User file");
            return;
        }
        if (args.length < 3) {
            switch (args[0]) {
                case "-h", "--help" -> {
                    System.out.println("Usage: java Main <password> -[e|d|ex|dx] <input file or directory path> [--fast|--safe]");
                    System.out.println("Options:");
                    System.out.println("  <password>         : The password used for encryption or decryption.");
                    System.out.println("  -e                 : Encrypt the specified file or directory.");
                    System.out.println("  -d                 : Decrypt the specified file or directory.");
                    System.out.println("  -ex                : Encrypt and delete the original file or directory.");
                    System.out.println("  -dx                : Decrypt and delete the original file or directory.");
                    System.out.println("  --fast             : Use EulaFast for higher speed and compression. Ideal for large datasets.");
                    System.out.println("  --safe             : Use EulaSafe for robust processing. Suitable for smaller, sensitive data.");
                }

                case "-v", "--version" -> System.out.println("Eula "+ Config.VERSION);
                case "-l", "--log" -> EulaCollections.openDir(Config.LOG_DIR);
                case "-g", "--generate" -> {
                    User.write();
                }
                default ->
                        System.out.println("Usage: java Main <password> -[e|d|ex|dx] <input file or directory path> [--fast|--safe]");
            }
            return;
        }

        String password = args[0];
        String option = args[1];
        String inputFile = args[2];
        boolean deleteFile = false;
        boolean encrypt = false;
        boolean useFast = false;
        boolean useSafe = false;

        if (option.endsWith("x")) {
            deleteFile = true;
            option = option.substring(0, option.length() - 1); // Remove 'x' from the option
        }

        // Validate option
        List<String> validOptions = Arrays.asList("-e", "-d", "-ex", "-dx");
        if (!validOptions.contains(option)) {
            System.out.println("Error: Invalid option. Use -e, -d, -ex, or -dx.");
            return;
        }

        // Validate input file or directory path
        File file = new File(inputFile);
        if (!file.exists() || !file.canRead()) {
            System.out.println("Error: Input path does not exist or cannot be read.");
            return;
        }

        // Checking for --fast or --safe arguments
        if (args.length > 3) {
            for (int i = 3; i < args.length; i++) {
                if ("--fast".equals(args[i])) {
                    useFast = true;
                } else if ("--safe".equals(args[i])) {
                    useSafe = true;
                }
            }
        }

        try{
            long start = System.currentTimeMillis();
            switch (option) {
                case "-e":
                    encrypt = true;
                    if (useFast) {
                        EulaFast.encrypt(password, inputFile, deleteFile);
                    } else if (useSafe) {
                        EulaSafe.encrypt(password, inputFile, deleteFile);
                    } else {
                        Eula.encrypt(password, inputFile, deleteFile);
                    }
                    break;
                case "-d":
                    if (useFast) {
                        EulaFast.decrypt(password, inputFile, deleteFile);
                    } else if (useSafe) {
                        EulaSafe.decrypt(password, inputFile, deleteFile);
                    } else {
                        Eula.decrypt(password, inputFile, deleteFile);
                    }
                    break;
                default:
                    System.out.println("Invalid option. Use -e or -d.");
            }
            long end = System.currentTimeMillis();
            System.out.println("Time taken : " + EulaCollections.formatTime(end - start));
            System.out.println("Done : " + inputFile + " " + (deleteFile ? "deleted" : "not deleted"));
        } catch (ExecutionException | InterruptedException | EulaException e) {
            if (encrypt){
                System.out.println("Encryption failed : " + inputFile);
            }else {
                System.out.println("Decryption failed : " + inputFile);
            }
            Log.err(e);
        }
    }
}
