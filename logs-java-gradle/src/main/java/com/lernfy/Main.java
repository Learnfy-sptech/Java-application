package com.lernfy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    public static void main(String[] args) {

        Logger generalLog = LoggerFactory.getLogger(Log.class);
        Log customLog = new Log(generalLog);


    }




}


