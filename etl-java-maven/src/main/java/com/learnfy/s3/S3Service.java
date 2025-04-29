package com.learnfy.s3;

import com.learnfy.ConfigLoader;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class S3Service {

    private static final S3Client s3Client = criarS3Client();

    public static S3Client criarS3Client() {
        String accessKey = ConfigLoader.get("AWS_ACCESS_KEY_ID");
        String secretKey = ConfigLoader.get("AWS_SECRET_ACCESS_KEY");
        String sessionToken = ConfigLoader.get("AWS_SESSION_TOKEN");
        String regionName = ConfigLoader.get("AWS_REGION");

        AwsSessionCredentials credentials = AwsSessionCredentials.create(accessKey, secretKey, sessionToken);

        return S3Client.builder()
                .region(Region.of(regionName))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

        public static List<String> listarArquivos(String bucketName, String prefixo) {
        System.out.println("Bucket Name: " + bucketName);
        List<String> arquivos = new ArrayList<>();
        if (bucketName == null || bucketName.isEmpty()) {
            throw new IllegalArgumentException("O nome do bucket não pode ser nulo ou vazio.");
        }

        try {
            ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(prefixo)
                    .build();

            ListObjectsV2Response listRes = s3Client.listObjectsV2(listReq);

            // Implementação de sempre fazer a inserção apenas no arquivo mais recente adicionado
            List<S3Object> objetos = new ArrayList<>(listRes.contents());
            objetos.sort((o1, o2) -> o2.lastModified().compareTo(o1.lastModified()));

            for (S3Object obj : objetos) {
                arquivos.add(obj.key());
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar os arquivos: " + e.getMessage());
        }
        return arquivos;
    }

    public static InputStream getArquivo(String bucketName, String key) {
        if (bucketName == null || bucketName.isEmpty()) {
            throw new IllegalArgumentException("O nome do bucket não pode ser nulo ou vazio.");
        }

        try {
            return s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
        } catch (Exception e) {
            System.err.println("Erro ao obter arquivo '" + key + "':" + e.getMessage());
            throw e;
        }
    }
}
