package com.learnfy.processador;

import com.learnfy.ConexaoBanco;
import com.learnfy.ConfigLoader;
import com.learnfy.ConversorCsv;
import com.learnfy.modelo.Empregabilidade;
import com.learnfy.s3.S3Service;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;
import com.learnfy.logs.LogService;

public class ProcessadorEmpregabilidade extends Processador {
    private final JdbcTemplate jdbcTemplate;
    private final S3Client s3Client;
    private final LogService logService;

    static {
        // Aumenta o limite para leitura de registros grandes (ex: 300MB ou mais)
        IOUtils.setByteArrayMaxOverride(500_000_000); // ou um valor ainda maior se necessário
    }

    public ProcessadorEmpregabilidade(JdbcTemplate jdbcTemplate, S3Client s3Client, LogService logService) {
        this.jdbcTemplate = jdbcTemplate;
        this.s3Client = s3Client;
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

            if (key.endsWith(".csv")) {
                workbook = ConversorCsv.convertCsvToXlsx(inputStream);
            }

            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("Planilha lida com sucesso. Processando linhas...");

            final int BATCH_SIZE = 300;
            List<Empregabilidade> batchEmpregabilidade = new ArrayList<>(BATCH_SIZE);

            Map<String, Integer> areasCadastradas = new HashMap<>();
            jdbcTemplate.query("SELECT id_area, nome FROM area_tb", rs -> {
                areasCadastradas.put(rs.getString("nome").trim().toLowerCase(), rs.getInt("id_area"));
            });

            System.out.println("Lendo áreas cadastradas:");
            jdbcTemplate.query("SELECT id_area, nome FROM area_tb", rs -> {
                String nome = rs.getString("nome");
                Integer id = rs.getInt("id_area");
                System.out.println("-> " + nome + " = " + id);
                areasCadastradas.put(nome.trim().toLowerCase(), id);
            });
            System.out.println("Total de áreas: " + areasCadastradas.size());

            Map<String, Integer> cboCadastrados = new HashMap<>();
            jdbcTemplate.query("SELECT cbo_2002 FROM dados_empregabilidade_tb", rs -> {
                cboCadastrados.put(rs.getString("nome").trim().toLowerCase(), rs.getInt("id_area"));
            });
            boolean primeiraLinha = true;

            Empregabilidade empregabilidade;

            empregabilidade = new Empregabilidade();

            for (Row row : sheet) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                try {
                    Empregabilidade dados = extrairDados(row);
                    batchEmpregabilidade.add(dados);

                    Integer cboInt = dados.getCbo2002();
                    if (cboInt != null) {
                        String cboStr = cboInt.toString();
                        if (!cboStr.isEmpty()) {
                            char primeiroDigito = cboStr.charAt(0); // exemplo: '1'
                            Integer primeiroDigitoInt = Character.getNumericValue(primeiroDigito);

                            // Suponha que seu Map tenha chaves como "1", "2", ..., "9"
                            if (primeiroDigitoInt != null) {
                                dados.setFk_area(primeiroDigitoInt);
                            } else {
                                System.err.println("Área não encontrada para CBO começando com: " + primeiroDigito + " (linha " + row.getRowNum() + ")");
                                dados.setFk_area(null);
                            }
                        }
                    }
                    String sigla = dados.getSiglaUf();
                    if (sigla != null && !sigla.isBlank()) {
                        try {
                            Integer fkUf = jdbcTemplate.queryForObject(
                                    "SELECT id_uf FROM uf_tb WHERE sigla = ?",
                                    Integer.class,
                                    sigla
                            );
                            dados.setFk_uf(fkUf);
                        } catch (Exception ex) {
                            System.err.println("UF não encontrada para sigla: " + sigla);
                        }
                    }


//
//                    Integer fkAreaPrimeiroIndex = jdbcTemplate.update(" INSERT INTO dados_empregabilidade_tb (fk_area)\n" +
//                            "    SELECT a.id\n" +
//                            "    FROM area a\n" +
//                            "    WHERE a.id = CAST(SUBSTRING(?, 1, 1) AS INTEGER)", empregabilidade.getCbo2002());
//                    empregabilidade.setFk_area(fkAreaPrimeiroIndex);
//
//                    Integer fkAreaPath = jdbcTemplate.update("""
//                            INSERT INTO dados_empregabilidade_tb (fk_uf)
//                            SELECT id_uf
//                            FROM uf_tb
//                            WHERE sigla IN (SELECT ? FROM dados_empregabilidade_tb);
//                            """, empregabilidade.getSiglaUf());
//                    empregabilidade.setFk_uf(fkAreaPath);

                    if (batchEmpregabilidade.size() == BATCH_SIZE) {
                        enviarBatch(batchEmpregabilidade);
                        batchEmpregabilidade.clear();
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao processar linha " + row.getRowNum() + ": " + e.getMessage());
                    e.printStackTrace();
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

    private String getStringOrNumericValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        } else {
            return "";
        }
    }


    private void enviarBatch(List<Empregabilidade> dadosEmpregabilidadeList) {
        System.out.println("Inserindo " + dadosEmpregabilidadeList.size() + " registros no banco.");

        String sql = "INSERT INTO dados_empregabilidade_tb (ano, sigla_uf, cbo_2002, cbo_2002_descricao, cbo_2002_descricao_familia, " +
                "categoria, grau_instrucao, salario_mensal, fk_area, fk_uf) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, dadosEmpregabilidadeList, dadosEmpregabilidadeList.size(), (ps, dados) -> {
            ps.setInt(1, dados.getAno() != null ? dados.getAno() : 0);
            ps.setString(2, dados.getSiglaUf() != null ? dados.getSiglaUf() : "");
            ps.setInt(3, dados.getCbo2002() != null ? dados.getCbo2002() : 0);
            ps.setString(4, dados.getCbo2002Descricao() != null ? dados.getCbo2002Descricao() : "");
            ps.setString(5, dados.getCbo2002DescricaoFamilia() != null ? dados.getCbo2002DescricaoFamilia() : "");
            ps.setString(6, dados.getCategoria() != null ? dados.getCategoria() : "");
            ps.setString(7, dados.getGrauInstrucao() != null ? dados.getGrauInstrucao() : "");
            ps.setDouble(8, dados.getSalarioMensal() != null ? dados.getSalarioMensal() : 0.0);
            ps.setInt(9, dados.getFk_area() != null ? dados.getFk_area() : 0);
            ps.setInt(10, dados.getFk_uf() != null ? dados.getFk_uf() : 0);
        });

//        jdbcTemplate.batchUpdate(sql, dadosEmpregabilidadeList, dadosEmpregabilidadeList.size(), (ps, dados) -> {
//            ps.setInt(1, dados.getAno() != null ? dados.getAno() : 0);
//            ps.setString(2, dados.getSiglaUf() != null ? dados.getSiglaUf() : "");
//            ps.setString(3, dados.getCbo2002() != null ? dados.getCbo2002() : "");
//            ps.setString(4, dados.getCbo2002Descricao() != null ? dados.getCbo2002Descricao() : "");
//            ps.setString(5, dados.getCbo2002DescricaoFamilia() != null ? dados.getCbo2002DescricaoFamilia() : "");
//            ps.setString(6, dados.getCategoria() != null ? dados.getCategoria() : "");
//            ps.setString(7, dados.getGrauInstrucao() != null ? dados.getGrauInstrucao() : "");
//            ps.setDouble(8, dados.getSalarioMensal() != null ? dados.getSalarioMensal() : 0.0);
//            ps.setInt(9, dados.getFk_area() != null ? dados.getFk_area() : 0);
//            ps.setInt(10, dados.getFk_uf() != null ? dados.getFk_uf() : 0);
//        });
        logService.registrarLog("BatchEmpregabilidade", "ProcessadorEmpregabilidade", "SUCESSO", "Sucesso na inserção de Batch");
    }

    public static void main(String[] args) {
        ZipSecureFile.setMinInflateRatio(0.0);

        IOUtils.setByteArrayMaxOverride(500_000_000); // Aumenta para 300MB

        String bucket = ConfigLoader.get("S3_BUCKET");
        S3Client s3Client = S3Service.criarS3Client();
        JdbcTemplate jdbcTemplate = ConexaoBanco.getJdbcTemplate();
        LogService logService = new LogService(jdbcTemplate);
        Processador processadorEmpregabilidade = new ProcessadorEmpregabilidade(jdbcTemplate, s3Client, logService);
        try {
            processadorEmpregabilidade.processar(bucket, "planilhas/dados_empregabilidade/vai-explodir-tratado.xlsx");
        } catch (Exception e) {
            System.out.println(String.format("Não foi possível processar os dados dos Cursos e Áreas, erro: %s", e.getMessage()));
        }
    }
}
