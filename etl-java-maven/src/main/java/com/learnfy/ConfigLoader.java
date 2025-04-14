package com.learnfy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static final Properties properties = new Properties();

    static {
        try {
            InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties");
            if (input == null) {
                throw new RuntimeException("Arquivo config.properties não encontrado no classpath.");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar config.properties: " + e.getMessage(), e);
        }
    }

    public static String get(String chave) {
        String valor = System.getenv(chave.toUpperCase());
        if (valor != null && !valor.isBlank()) return valor;

        return properties.getProperty(chave);
    }
}

