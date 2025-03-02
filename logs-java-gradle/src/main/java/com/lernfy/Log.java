package com.lernfy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Log {
    final Logger log;

    public Log(Logger log) {
        this.log = log;
    }

    public void titleLog() {
        String[] title = {
                "",
                "  ##       #######     ###     ######   ##    ##  #######  #######  ##    ##  ",
                "  ##       ##         #   #    ##   ##  ###   ##    ##     ##        ##  ##  ",
                "  ##       #######   #######   ######   ## #  ##    ##     #######    ####  ",
                "  ##       ##       ##     ##  ##  ##   ##  # ##    ##     ##          ##  ",
                "  #######  #######  ##     ##  ##   ##  ##   ###  #######  ##          ##  ",
                "",
        };

        for (String line : title) {
            System.out.println(line);
        }
        System.out.println("Iniciando a coleta de dados para o projeto LEARNIFY...");
    }

    public void newFileLog(String file) {
        log.info(String.format("Acessando o arquivo %s", file));
    }

    public void newWorkbook(String workbook) {
        log.info(String.format("Arquivo que será lido: %s", workbook));
    }

    public void newWorksheet(String worksheet) {
        log.info(String.format("Planilha atual: %s", worksheet));
    }


    // Arrumar para que em alguns caso o dado coletado seja nulo e, se o número de dados coletados nulos exceder a uma certa quantidade, mudar de info para um warn log
    public void dataColumn(String column, Integer amountLines, Integer minValue, Integer maxValue) {
        for (int i = 1; i < amountLines; i++) {
            log.info(String.format("Coletando dados da coluna %s, Dado coletado - %d", column, ThreadLocalRandom.current().nextInt(minValue, maxValue+1)));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                    e.printStackTrace();
            }
        }
        log.info(String.format("Dados coletados: %d | Células Vazias: %d", amountLines, ThreadLocalRandom.current().nextInt(0,amountLines+1)));
    }

    public void dataColumn(String column, Integer amountLines, Double minValue, Double maxValue) {
        for (int i = 1; i < amountLines; i++) {
            log.info(String.format("Coletando dados da coluna %s, Dado coletado - %.2f", column, ThreadLocalRandom.current().nextDouble(minValue, maxValue+1)));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info(String.format("Dados coletados: %d | Células Vazias: %d", amountLines, ThreadLocalRandom.current().nextInt(0,amountLines+1)));
    }

    public void dataColumn(String column, Integer amountLines, String[] listaValores) {
        for (int i = 1; i < amountLines; i++) {
            log.info(String.format("Coletando dados da coluna %s, Dado coletado - %s", column, listaValores[i]));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info(String.format("Dados coletados: %d | Células Vazias: %d", amountLines, ThreadLocalRandom.current().nextInt(0,amountLines+1)));
    }

    public void finalMessage(String file, String[] nameWorkSheets) {
        log.info(String.format("FIM DA TRANSFORMAÇÃO E LEITURA DOS DADOS | Arquivo: %s, Planilhas %s", file, Arrays.toString(nameWorkSheets)));
    }

}