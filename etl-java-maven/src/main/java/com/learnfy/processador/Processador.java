package com.learnfy.processador;
//Interface
public abstract class Processador {
    public abstract void processar(String bucket, String key) throws Exception;
}
