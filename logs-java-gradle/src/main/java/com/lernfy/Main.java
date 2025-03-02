package com.lernfy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    public static void main(String[] args) {

        Logger generalLog = LoggerFactory.getLogger(Log.class);
        Log customLog = new Log(generalLog);

        customLog.titleLog();

        String[] schoolNames = {
                "Colégio Horizonte Azul",
                "Escola Estrela do Saber",
                "Instituto Aurora",
                "Escola do Sol Nascente",
                "Colégio Ventos do Norte",
                "Escola Jardim das Letras",
                "Instituto Einstein",
                "Colégio Pioneiros da Educação",
                "Escola do Amanhã",
                "Colégio das Águas Claras",
                "Escola Monte Verde",
                "Instituto Futuro Brilhante",
                "Colégio Arco-Íris",
                "Escola de Excelência",
                "Instituto São Pedro",
                "Colégio Nova Geração",
                "Escola Viver e Aprender",
                "Instituto Educacional Flor do Campo",
                "Colégio Lumiar",
                "Escola Brilho do Conhecimento",
                "Instituto Mente Aberta",
                "Colégio Valor e Virtude",
                "Escola Vida Plena",
                "Instituto Integração",
                "Colégio Paz e Saber",
                "Escola de Talentos",
                "Instituto Evolução",
                "Colégio Criar e Aprender",
                "Escola da Esperança",
                "Instituto Raízes do Saber"
        };


        Integer amountLines = schoolNames.length;
        String workbook = "ENEM 2021";
        String[] worksheets = {"Notas por Área de Conhecimento"};

        customLog.newWorkbook(workbook);
        customLog.newWorksheet(worksheets[0]);
        customLog.dataColumn("Escola", amountLines, schoolNames);
        customLog.dataColumn("Idade", amountLines, 18, 42);
        customLog.dataColumn("NotaMatemática", amountLines, 100, 100);
        customLog.dataColumn("NotaCiênciasHumanas", amountLines, 100, 1000);
        customLog.dataColumn("NotaCiênciasNatureza", amountLines, 100, 1000);
        customLog.dataColumn("Português", amountLines, 100, 1000);
        customLog.dataColumn("Redação", amountLines, 100, 1000);
        customLog.finalMessage(workbook, worksheets);

    }




}


