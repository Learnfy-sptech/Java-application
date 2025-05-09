package com.learnfy;

import com.learnfy.logs.LogService;
import com.learnfy.processador.*;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;
import com.learnfy.s3.S3Service;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String bucket = ConfigLoader.get("S3_BUCKET");
        S3Client s3Client = S3Service.criarS3Client();
        String prefixo = "planilhas/";
        JdbcTemplate jdbcTemplate = ConexaoBanco.getJdbcTemplate();
        LogService logService = new LogService(jdbcTemplate);

        List<String> arquivos = S3Service.listarArquivos(bucket, prefixo);

        if (!arquivos.isEmpty()) {
            String arquivoMaisRecente = arquivos.getFirst();

            if (arquivoMaisRecente.endsWith(".xlsx")) {
                Processador processador = ProcessadorFactory.getProcessador(arquivoMaisRecente, jdbcTemplate, s3Client, bucket, logService);
                try {
                    processador.processar(bucket, arquivoMaisRecente);
                } catch (Exception e) {
                    System.out.println(String.format("Houve um erro ao inserir os dados da tabela %s", arquivoMaisRecente));
                }
            } else {
                logService.registrarLog("Main.java", "Indefinido", "CRÍTICO", "Tipo de arquivo inválido");
            }
        }
    }
}