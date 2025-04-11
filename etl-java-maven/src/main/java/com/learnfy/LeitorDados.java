package com.learnfy;
import org.apache.poi.ss.usermodel.*;
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

            try {
                switch (i) {
                    case 0 -> curso.setAno((int) getNumericValue(cell));
                    case 1 -> curso.setSigla_uf(getStringValue(cell));
                    case 2 -> curso.setNome_curso_cine(getStringValue(cell));
                    case 3 -> curso.setNome_area_geral(getStringValue(cell));
                    case 4 -> curso.setQuantidade_vagas_processos_seletivos((int) getNumericValue(cell));
                    case 5 -> curso.setQuantidade_inscritos((int) getNumericValue(cell));
                    case 6 -> curso.setQuantidade_inscritos_ead((int) getNumericValue(cell));
                    case 7 -> curso.setQuantidade_ingressantes_60_mais((int) getNumericValue(cell));
                    case 8 -> curso.setQuantidade_matriculas((int) getNumericValue(cell));
                    case 9 -> curso.setQuantidade_concluintes((int) getNumericValue(cell));
                }
            } catch (Exception e) {
                System.out.printf("Erro ao ler célula %d da linha %d: %s%n", i, row.getRowNum(), e.getMessage());
            }
        }

        return curso;
    }

    private String getStringValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> throw new IllegalStateException("Tipo inválido para String: " + cell.getCellType());
        };
    }

    private double getNumericValue(Cell cell) {
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    throw new IllegalStateException("Valor não numérico: " + cell.getStringCellValue());
                }
            }
            default -> throw new IllegalStateException("Tipo inválido para número: " + cell.getCellType());
        };
    }

    public void enviarDados(DadosCurso curso, int linhaAtual) {
        System.out.println("Inserindo linha: " + linhaAtual);
        jdbcTemplate.update("INSERT INTO dados_curso (" +
                        "ano, sigla_uf, nome_curso_cine, nome_area_geral, " +
                        "quantidade_vagas_processos_seletivos, quantidade_inscritos, quantidade_inscritos_ead, " +
                        "quantidade_ingressantes_60_mais, quantidade_matriculas, quantidade_concluintes) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                curso.getAno(),
                curso.getSigla_uf(),
                curso.getNome_curso_cine(),
                curso.getNome_area_geral(),
                curso.getQuantidade_vagas_processos_seletivos(),
                curso.getQuantidade_inscritos(),
                curso.getQuantidade_inscritos_ead(),
                curso.getQuantidade_ingressantes_60_mais(),
                curso.getQuantidade_matriculas(),
                curso.getQuantidade_concluintes()
        );
    }

    public void varrerPlanilha(Workbook workbook) {
        for (Sheet sheet : workbook) {
            for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                enviarDados(extrairDados(row),i);
            }
        }
    }
}

