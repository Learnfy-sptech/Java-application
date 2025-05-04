package com.learnfy.processador;

import com.learnfy.logs.LogService;
import com.learnfy.modelo.Empregabilidade;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
    private final LogService logService;

    public ProcessadorEmpregabilidade(JdbcTemplate jdbcTemplate, S3Client s3Client, String bucketName, LogService logService) {
        this.jdbcTemplate = jdbcTemplate;
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.logService = logService;
    }

    @Override
    public void processar(String bucket, String key) throws Exception {
        System.out.println("Iniciando processamento do arquivo: " + key);
        logService.registrarLog(key, "ProcessadorEmpregabilidade", "START", "Iniciando processamento do arquivo.");

        try (InputStream inputStream = s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build())) {

            Workbook workbook;
            if (key.endsWith(".xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else {
                workbook = new XSSFWorkbook(inputStream);
            }

            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("Planilha lida com sucesso. Processando linhas...");

            final int BATCH_SIZE = 300;
            List<Empregabilidade> batchEmpregabilidade = new ArrayList<>(BATCH_SIZE);

            boolean primeiraLinha = true;

            for (Row row : sheet) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                try {
                    Empregabilidade dados = extrairDados(row);
                    batchEmpregabilidade.add(dados);

                    if (batchEmpregabilidade.size() == BATCH_SIZE) {
                        enviarBatch(batchEmpregabilidade);
                        batchEmpregabilidade.clear();
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao processar linha " + row.getRowNum() + ": " + e.getMessage());
                }
            }

            if (!batchEmpregabilidade.isEmpty()) {
                enviarBatch(batchEmpregabilidade);
                batchEmpregabilidade.clear();
            }

            workbook.close();
            System.out.println("✔ Leitura da planilha '" + key + "' finalizada.");
            logService.registrarLog(key, "ProcessadorEmpregabilidade", "SUCESSO", "Processamento finalizado com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao processar a planilha '" + key + "': " + e.getMessage());
            logService.registrarLog(key, "ProcessadorEmpregabilidade", "CRITICO", "Erro ao processar planilha: " + e.getMessage());
        }
    }

    private Empregabilidade extrairDados(Row row) {
        Empregabilidade dados = new Empregabilidade();
        try {
            for (Cell cell : row) {
                switch (cell.getColumnIndex()) {
                    case 0 -> dados.setAno((int) getNumericValue(cell));
                    case 1 -> dados.setSiglaUf(getStringValue(cell));
                    case 2 -> dados.setCbo2002((int) getNumericValue(cell));
                    case 3 -> dados.setCbo2002Descricao(getStringValue(cell));
                    case 4 -> dados.setCbo2002DescricaoFamilia(getStringValue(cell));
                    case 5 -> dados.setCategoria(getStringValue(cell));
                    case 6 -> dados.setGrauInstrucao(getStringValue(cell));
                    case 7 -> dados.setSalarioMensal(getNumericValue(cell));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao extrair dados da linha: " + e.getMessage());
            logService.registrarLog("LinhaEmpregabilidade", "ProcessadorEmpregabilidade", "ALERTA", "Erro ao extrair dados da linha: " + e.getMessage());
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
            ps.setString(2, dados.getSiglaUf() != null ? dados.getSiglaUf() : "");
            ps.setInt(3, dados.getCbo2002() != null ? dados.getCbo2002() : 0);
            ps.setString(4, dados.getCbo2002Descricao() != null ? dados.getCbo2002Descricao() : "");
            ps.setString(5, dados.getCbo2002DescricaoFamilia() != null ? dados.getCbo2002DescricaoFamilia() : "");
            ps.setString(6, dados.getCategoria() != null ? dados.getCategoria() : "");
            ps.setString(7, dados.getGrauInstrucao() != null ? dados.getGrauInstrucao() : "");
            ps.setDouble(8, dados.getSalarioMensal() != null ? dados.getSalarioMensal() : 0.0);
        });
        logService.registrarLog("BatchEmpregabilidade", "ProcessadorEmpregabilidade", "SUCESSO", "Sucesso na inserção de Batch");
    }
}
