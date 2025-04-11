package com.learnfy;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        // Conexões com a S3
        String bucketName = "learnfy-database";
        String prefixo = "planilhas/";

        List<String> arquivos = listarArquivosS3(bucketName, prefixo);

        for (String key : arquivos) {
            if (key.endsWith(".xlsx") || key.endsWith(".xls") || key.endsWith(".csv")){
                String nomeArquivoLocal = key.substring(key.lastIndexOf("/") + 1);
                downloadFromS3(bucketName, key, nomeArquivoLocal);
                readExcel(nomeArquivoLocal);
            }
        }
    }

    public static List<String> listarArquivosS3(String bucketName, String prefixo) {
        List<String> arquivos = new ArrayList<>();
        Region region = Region.US_EAST_1;

        try (S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build()) {
            ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(prefixo)
                    .build();

            ListObjectsV2Response listRes = s3.listObjectsV2(listReq);

            for (S3Object obj : listRes.contents()) {
                arquivos.add(obj.key());
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar os arquivos: " + e.getMessage() );
        }

        return arquivos;
    }

    public static void downloadFromS3(String bucketName, String key, String downloadPath) {
        Region region = Region.US_EAST_1;

        try (S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create()) // usa o perfil default ~/.aws/credentials
                .build()) {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            Path path = Paths.get(downloadPath);
            s3.getObject(request, path);
            System.out.println("✔ Arquivo baixado com sucesso de " + bucketName + "/" + key);
        } catch (Exception e) {
            System.err.println("Erro ao baixar do S3: " + e.getMessage());
        }
    }

    public static void readExcel(String filePath) {
        try (InputStream file = new FileInputStream(filePath)) {
            Workbook workbook = new XSSFWorkbook(file);
            ConexaoBanco conexao = new ConexaoBanco();
            JdbcTemplate jdbcTemplate = conexao.getJdbcTemplate();
            LeitorDados leitorArquivo01 = new LeitorDados(jdbcTemplate);
            leitorArquivo01.varrerPlanilha(workbook);
            workbook.close();
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo Excel: " + e.getMessage());
        }
    }
}