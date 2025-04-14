package com.learnfy;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException {
        String bucketName = "learnfy-database";
        String prefixo = "planilhas/";

        S3Client s3Client = criarS3Client();
        List<String> arquivos = listarArquivosS3(s3Client, bucketName, prefixo);

        for (String key : arquivos) {
            if (key.endsWith(".xlsx") || key.endsWith(".xls") || key.endsWith(".csv")) {
                lerPlanilhaDiretoDoS3(s3Client, bucketName, key);
            }
        }
        s3Client.close();
    }

    public static S3Client criarS3Client() {
        String accessKey = ConfigLoader.get("AWS_ACCESS_KEY_ID");
        String secretKey = ConfigLoader.get("AWS_SECRET_ACCESS_KEY");
        String regionName = ConfigLoader.get("AWS_REGION");

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(regionName))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    public static List<String> listarArquivosS3(S3Client s3, String bucketName, String prefixo) {
        List<String> arquivos = new ArrayList<>();
        try {
            ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(prefixo)
                    .build();

            ListObjectsV2Response listRes = s3.listObjectsV2(listReq);

            for (S3Object obj : listRes.contents()) {
                arquivos.add(obj.key());
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar os arquivos: " + e.getMessage());
        }
        return arquivos;
    }

    public static void lerPlanilhaDiretoDoS3(S3Client s3, String bucketName, String key) {
        try (InputStream s3Stream = s3.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build())) {

            Workbook workbook = new XSSFWorkbook(s3Stream);

            ConexaoBanco conexao = new ConexaoBanco();
            JdbcTemplate jdbcTemplate = conexao.getJdbcTemplate();
            LeitorDados leitor = new LeitorDados(jdbcTemplate);
            leitor.varrerPlanilha(workbook);

            workbook.close();

            System.out.println("✔ Leitura da planilha '" + key + "' finalizada.");
        } catch (Exception e) {
            System.err.println("Erro ao processar a planilha '" + key + "': " + e.getMessage());
        }
    }
}