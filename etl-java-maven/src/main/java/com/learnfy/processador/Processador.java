package com.learnfy.processador;

public interface Processador {
    void processar(String bucket, String key) throws Exception;
}
