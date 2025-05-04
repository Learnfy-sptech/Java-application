package com.learnfy;

import com.learnfy.logs.LogService;
import com.learnfy.processador.*;
import org.apache.commons.math3.analysis.function.Log;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;
import com.learnfy.s3.S3Service;

import java.util.List;

import static com.learnfy.processador.ProcessadorFactory.inserirDadosEscolaridade;

public class Main {
    public static void main(String[] args) {
        String bucket = ConfigLoader.get("S3_BUCKET");
        S3Client s3Client = S3Service.criarS3Client();
        String prefixo = "planilhas/";
        JdbcTemplate jdbcTemplate = ConexaoBanco.getJdbcTemplate();
        LogService logService = new LogService(jdbcTemplate);

        /*
        Altere o valor dessa variável para false caso seja a
        primeira inserção ou queira inserir todos os dados novamente
         */
        Boolean primeiravez = true;

        if (primeiravez) {
            ProcessadorFactory.inserirDadosEscolaridade(jdbcTemplate, s3Client, bucket, logService);
        } else {
            List<String> arquivos = S3Service.listarArquivos(bucket, prefixo);

            if (!arquivos.isEmpty()) {
                String arquivoMaisRecente = arquivos.getFirst();

                if (arquivoMaisRecente.endsWith(".xlsx") || arquivoMaisRecente.endsWith(".xls") || arquivoMaisRecente.endsWith(".csv")) {
                    Processador processador = ProcessadorFactory.getProcessador(arquivoMaisRecente, jdbcTemplate, s3Client, bucket, logService);
                    try {
                        processador.processar(bucket, arquivoMaisRecente);
                    } catch (Exception e) {
                        System.out.println(String.format("Houve um erro ao inserir os dados da tabela %s", arquivoMaisRecente));
                    }
                }
            }
        }
    }
}