package com.learnfy;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {

        ConexaoBanco conexao = new ConexaoBanco();
        JdbcTemplate jdbcTemplate = conexao.getJdbcTemplate();

        Path caminho = Path.of("C:/Users/Administrador/Documents/base-dados-cursos-ensino-superior-reduzido.xlsx");
        InputStream arquivo = Files.newInputStream(caminho);

        IOUtils.setByteArrayMaxOverride(2000_000_000);

        Workbook workbook = new XSSFWorkbook(arquivo);

        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i < sheet.getLastRowNum()+1; i++) {
            for (int j = 0; j < sheet.getRow(i).getLastCellNum(); j++) {
                Cell cell = sheet.getRow(i).getCell(j);
                switch (j) {
                    case 0:
                        ano = (int) cell.getNumericCellValue();
                        break;
                    case 1:
                        sigla_uf = cell.getStringCellValue();
                        break;
                    case 2:
                        id_municipio = (int) cell.getNumericCellValue();
                        break;
                    case 3:
                        tipo_dimensao = (int) cell.getNumericCellValue();
                        break;
                    case 4:
                        tipo_organizacao_academica = (int) cell.getNumericCellValue();
                        break;
                    case 5:
                        tipo_organizacao_administrativa = (int) cell.getNumericCellValue();
                        break;
                    case 6:
                        rede = (int) cell.getNumericCellValue();
                        break;
                    case 7:
                        id_ies = (int) cell.getNumericCellValue();
                        break;
                    case 8:
                        nome_curso = cell.getStringCellValue();
                        break;
                }
            }
            jdbcTemplate.update("INSERT INTO cursos VALUES (null,?,?,?,?,?,?,?,?)", ano, sigla_uf, id_municipio, tipo_dimensao, tipo_organizacao_academica, tipo_organizacao_administrativa, rede, nome_curso);
        }
    }
}