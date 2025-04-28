package com.learnfy.processador;

import com.learnfy.modelo.Empregabilidade;
import org.apache.poi.ss.usermodel.*;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProcessadorEmpregabilidade implements Processador {
    private final JdbcTemplate jdbcTemplate;
    private final S3Client s3Client;
    private final String bucketName;

    public ProcessadorEmpregabilidade(JdbcTemplate jdbcTemplate, S3Client s3Client, String bucketName) {
        this.jdbcTemplate = jdbcTemplate;
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public void processar(String bucket, String key) throws Exception {
        System.out.println("Iniciando processamento do arquivo: " + key);

        try (InputStream inputStream = s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build())) {

            Workbook workbook = WorkbookFactory.create(inputStream);
            List<Empregabilidade> dadosEmpregabilidadeList = new ArrayList<>();

            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("Planilha lida com sucesso. Processando linhas...");

            boolean primeiraLinha = true;

            for (Row row : sheet) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                try {
                    Empregabilidade dados = extrairDados(row);
                    dadosEmpregabilidadeList.add(dados);
                } catch (Exception e) {
                    System.err.println("Erro ao processar linha " + row.getRowNum() + ": " + e.getMessage());
                }
            }

            final int BATCH_SIZE = 500;
            for (int i = 0; i < dadosEmpregabilidadeList.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, dadosEmpregabilidadeList.size());
                List<Empregabilidade> subList = dadosEmpregabilidadeList.subList(i, end);
                enviarBatch(subList);
            }

            workbook.close();
            System.out.println("✔ Leitura da planilha '" + key + "' finalizada.");
        } catch (Exception e) {
            System.err.println("Erro ao processar a planilha '" + key + "': " + e.getMessage());
        }
    }

    private Empregabilidade extrairDados(Row row) {
        Empregabilidade dados = new Empregabilidade();
        try {
            for (Cell cell : row) {
                switch (cell.getColumnIndex()) {
                    case 0 -> dados.setAno((int) getNumericValue(cell));
                    case 1 -> dados.setSigla_uf(getStringValue(cell));
                    case 2 -> dados.setCbo_2002((int) getNumericValue(cell));
                    case 3 -> dados.setCbo_2002_descricao(getStringValue(cell));
                    case 4 -> dados.setCbo_2002_descricao_familia(getStringValue(cell));
                    case 5 -> dados.setCategoria(getStringValue(cell));
                    case 6 -> dados.setGrau_instrucao(getStringValue(cell));
                    case 7 -> dados.setSalario_mensal(getNumericValue(cell));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao extrair dados da linha: " + e.getMessage());
        }
        return dados;
    }

    private double getNumericValue(Cell cell) {
        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : 0;
    }

    private String getStringValue(Cell cell) {
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : "";
    }

    private void enviarBatch(List<Empregabilidade> dadosEmpregabilidadeList) {
        System.out.println("Inserindo " + dadosEmpregabilidadeList.size() + " registros no banco.");

        String sql = "INSERT INTO empregabilidade (ano, sigla_uf, cbo_2002, cbo_2002_descricao, cbo_2002_descricao_familia, " +
                "categoria, grau_instrucao, salario_mensal) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, dadosEmpregabilidadeList, dadosEmpregabilidadeList.size(), (ps, dados) -> {
            ps.setInt(1, dados.getAno() != null ? dados.getAno() : 0);
            ps.setString(2, dados.getSigla_uf() != null ? dados.getSigla_uf() : "");
            ps.setInt(3, dados.getCbo_2002() != null ? dados.getCbo_2002() : 0);
            ps.setString(4, dados.getCbo_2002_descricao() != null ? dados.getCbo_2002_descricao() : "");
            ps.setString(5, dados.getCbo_2002_descricao_familia() != null ? dados.getCbo_2002_descricao_familia() : "");
            ps.setString(6, dados.getCategoria() != null ? dados.getCategoria() : "");
            ps.setString(7, dados.getGrau_instrucao() != null ? dados.getGrau_instrucao() : "");
            ps.setDouble(8, dados.getSalario_mensal() != null ? dados.getSalario_mensal() : 0.0);
        });
    }
}
