package com.learnfy;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class ConversorCsv {
    public static Workbook convertCsvToXlsx(InputStream csvInputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvInputStream))) {
            String line;
            int rowNum = 0;

            while ((line = reader.readLine()) != null) {
                Row row = sheet.createRow(rowNum++);
                String[] cells = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                for (int i = 0; i < cells.length; i++) {
                    String cellValue = cells[i].replaceAll("^\"|\"$", "");
                    row.createCell(i).setCellValue(cellValue);
                }
            }
        }
        return workbook;
    }
}

