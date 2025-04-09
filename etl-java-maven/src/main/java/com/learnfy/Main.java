package com.learnfy;

import org.apache.poi.ss.usermodel.Workbook;
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
        LeitorDados leitorArquivo01 = new LeitorDados(jdbcTemplate);

        Path path = Path.of("C:/Users/Administrador/Documents/base-dados-cursos-ensino-superior-reduzido.xlsx");
        InputStream file = Files.newInputStream(path);

        Workbook workbook = new XSSFWorkbook(file);
        leitorArquivo01.varrerPlanilha(workbook);

    }
}