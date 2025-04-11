package com.learnfy;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    static {
        try {
            FileInputStream input = new FileInputStream("config/config.properties");
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Erro ao carregar config.properties: " + e.getMessage());
        }
    }

    public static String get(String chave) {
        return properties.getProperty(chave);
    }
}

