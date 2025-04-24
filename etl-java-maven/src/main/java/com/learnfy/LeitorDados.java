package com.learnfy;
import org.apache.poi.ss.usermodel.*;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeitorDados {

    private JdbcTemplate jdbcTemplate;

    public LeitorDados(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DadosEmpregabilidade extrairDados(Row row) {
        DadosEmpregabilidade empregos = new DadosEmpregabilidade();

        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell == null) continue;

            try {
                switch (i) {
                    case 0 -> empregos.setAno((int) getNumericValue(cell));
                    case 1 -> empregos.setSigla_uf(getStringValue(cell));
                    case 2 -> empregos.setCbo_2002((int) getNumericValue(cell));
                    case 3 -> empregos.setcbo_2002_descricao(getStringValue(cell));
                    case 4 -> empregos.setcbo_2002_descricao_familia(getStringValue(cell));
                    case 5 -> empregos.setCategoria(getStringValue(cell));
                    case 6 -> empregos.setgrau_instrucao(getStringValue(cell));
                    case 7 -> empregos.setSalario_mensal(getNumericValue(cell));
                }
            } catch (Exception e) {
                System.out.printf("Erro ao ler célula %d da linha %d: %s%n", i, row.getRowNum(), e.getMessage());
            }
        }

        return empregos;
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

    public void enviarBatch(List<DadosEmpregabilidade> empregoss) {
        String sql = "INSERT INTO dados_empregos (ano, sigla_uf, cbo_2002, cbo_2002_descricao, cbo_2002_descricao_familia," +
                "categoria, grau_instrucao, salario_mensal) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                DadosEmpregabilidade empregos = empregoss.get(i);
                ps.setInt(0, empregos.getAno());
                ps.setString(1, empregos.getSigla_uf());
                ps.setInt(2, empregos.getCbo_2002());
                ps.setString(3, empregos.getcbo_2002_descricao());
                ps.setString(4, empregos.getcbo_2002_descricao_familia());
                ps.setString(5, empregos.getCategoria());
                ps.setString(6, empregos.getgrau_instrucao());
                ps.setDouble(7, empregos.getSalario_mensal());

                System.out.printf("Inserindo empregos: [%d | %s | %d | %s | %s | %s | %s | %.2f]%n",
                        empregos.getAno(),
                        empregos.getSigla_uf(),
                        empregos.getCbo_2002(),
                        empregos.getcbo_2002_descricao(),
                        empregos.getcbo_2002_descricao_familia(),
                        empregos.getCategoria(),
                        empregos.getgrau_instrucao(),
                        empregos.getSalario_mensal()
                );
            }

            public int getBatchSize() {
                return empregoss.size();
            }
        });

        System.out.println("Batch de " + empregoss.size() + " registros inserido com sucesso!");
    }

    public void varrerPlanilha(Workbook workbook) {
        List<DadosEmpregabilidade> empregoss = new ArrayList<>();

        Sheet sheet = workbook.getSheetAt(0);
        boolean primeiraLinha = true;

        for (Row row : sheet) {
            if (primeiraLinha) {
                primeiraLinha = false;
                continue;
            }

            try {
                DadosEmpregabilidade empregos = extrairDados(row);
                if (empregos != null) {
                    empregoss.add(empregos);
                }
            } catch (Exception e) {
                System.err.println("Erro ao processar linha " + row.getRowNum() + ": " + e.getMessage());
            }
        }

        final int BATCH_SIZE = 500;
        for (int i = 0; i < empregoss.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, empregoss.size());
            List<DadosEmpregabilidade> subList = empregoss.subList(i, end);
            enviarBatch(subList);
        }
    }
}

