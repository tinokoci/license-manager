package dev.strongtino.soteria.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ConfigFile {

    private static boolean initialized;

    private final static String DIRECTORY = "Soteria";
    private final static String FILE = "config.properties";

    private Properties properties;

    public ConfigFile() {
        if (initialized) return;

        File directory = new File(DIRECTORY);

        if (!directory.exists() && !directory.mkdir()) {
            System.out.println("Couldn't create the " + DIRECTORY + "directory");
            System.exit(1);
        }
        File file = new File(DIRECTORY + File.separator + FILE);

        if (!file.exists()) {
            try (InputStream in = ConfigFile.class.getClassLoader().getResourceAsStream(FILE);
                 OutputStream out = new FileOutputStream(DIRECTORY + File.separator + FILE)) {

                if (in == null) return;
                int data;

                while ((data = in.read()) != -1) {
                    out.write(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        properties = new Properties();

        try {
            FileInputStream stream = new FileInputStream(DIRECTORY + File.separator + FILE);
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        initialized = true;
    }

    public String getString(String key) {
        return properties.getProperty(key);
    }

    public int getInteger(String key) {
        try {
            return Integer.parseInt(getString(key));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}