package com.learnfy.processador;
//Interface
public interface Processador {
    void processar(String bucket, String key) throws Exception;
}
