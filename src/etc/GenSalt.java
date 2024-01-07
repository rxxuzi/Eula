package etc;

import eula.EulaException;
import global.Config;
import global.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenSalt {
    public static void genSalt(String filename, int length) throws EulaException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("# This is an automatically generated salt for cryptographic purposes.\n");
            writer.write("# It contains a unique, random sequence of characters to enhance security during encryption processes.\n");
            writer.write("# Please do not modify this text to ensure the integrity of the cryptographic operations.\n\n");

            Random random = new Random();

            for (int i = 0; i < length; ++i) {
                char randomChar = (char) (random.nextInt(126 - 32 + 1) + 32);
                writer.write(randomChar);
            }

            Log.info("GenSalt file generated successfully.");
        } catch (IOException e) {
            throw new EulaException("Failed to generate salt file.",e);
        }
    }

    public static void getSalt() throws EulaException {
        genSalt(Config.SALT_PATH , 200);
    }
}
