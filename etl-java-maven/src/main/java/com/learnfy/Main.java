package com.learnfy;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;
import com.learnfy.processador.Processador;
import com.learnfy.processador.ProcessadorFactory;
import com.learnfy.s3.S3Service;

public class Main {
    public static void main(String[] args) throws Exception {
        String bucket = ConfigLoader.get("S3_BUCKET");
        S3Client s3Client = S3Client.create();
        String prefixo = "planilhas/";

        JdbcTemplate jdbcTemplate = ConexaoBanco.getJdbcTemplate();

        List<String> arquivos = S3Service.listarArquivos(bucket, prefixo);

        for (String key : arquivos) {
            if (key.endsWith(".xlsx") || key.endsWith(".xls") || key.endsWith(".csv")) {
                Processador processador = ProcessadorFactory.getProcessador(key, jdbcTemplate, s3Client, bucket);
                processador.processar(bucket, key);
            }
        }
    }
}