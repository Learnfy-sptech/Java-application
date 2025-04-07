package com.learnfy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.jdbc.core.JdbcTemplate;

public class LeitorDados {

    private JdbcTemplate jdbcTemplate;

    public LeitorDados(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DadosCurso extrairDados(Row row) {
        DadosCurso curso = new DadosCurso();
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell == null) continue;
            switch (i) {
                case 0 -> curso.setAno((int) cell.getNumericCellValue());
                case 1 -> curso.setSigla_uf(cell.getStringCellValue());
                case 2 -> curso.setId_municipio((int) cell.getNumericCellValue());
                case 3 -> curso.setTipo_dimensao((int) cell.getNumericCellValue());
                case 4 -> curso.setTipo_organizacao_academica((int) cell.getNumericCellValue());
                case 5 -> curso.setTipo_organizacao_administrativa((int) cell.getNumericCellValue());
                case 6 -> curso.setRede((int) cell.getNumericCellValue());
                case 7 -> curso.setNome_curso(cell.getStringCellValue());
            }
        }
        return curso;
    }

    public void enviarDados(DadosCurso curso) {
        jdbcTemplate.update("INSERT INTO cursos VALUES (null,?,?,?,?,?,?,?,?);",
                curso.getAno(), curso.getSigla_uf(), curso.getId_municipio(), curso.getTipo_dimensao(),
                curso.getTipo_organizacao_academica(), curso.getTipo_organizacao_administrativa(),
                curso.getRede(), curso.getNome_curso());
    }

    public void varrerPlanilha(Workbook workbook) {
        for (Sheet sheet : workbook) {
            for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
                Row row = sheet.getRow(i);
                enviarDados(extrairDados(row));
            }
        }
    }

}

