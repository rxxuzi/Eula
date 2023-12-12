package main;

import eula.Eula;

import java.util.concurrent.ExecutionException;

public class Eulax {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java Eulax <password> -[e|d|ex|dx] <input file path>");
            return;
        }

        String password = args[0];
        String option = args[1];
        String inputFile = args[2];
        boolean deleteFile = false;
        boolean encrypt = false;

        if (option.endsWith("x")) {
            deleteFile = true;
            option = option.substring(0, option.length() - 1); // Remove 'x' from the option
        }

        try{
            switch (option) {
                case "-e":
                    encrypt = true;
                    Eula.encrypt(password, inputFile,deleteFile);
                    break;
                case "-d":
                    Eula.decrypt(password, inputFile, deleteFile);
                    break;
                default:
                    System.out.println("Invalid option: " + option);
            }
            System.out.println("Done : " + inputFile + " " + (deleteFile ? "deleted" : "not deleted"));
        } catch (ExecutionException | InterruptedException e) {
            if (encrypt){
                System.out.println("Encryption failed : " + inputFile);
            }else {
                System.out.println("Decryption failed : " + inputFile);
            }
        }
    }
}
