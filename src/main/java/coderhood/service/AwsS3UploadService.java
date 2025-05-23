package coderhood.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3UploadService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String uploadTiff(MultipartFile file, String fileName) {
        try {
            String finalFileName = (fileName == null || fileName.isBlank())
                    ? "tiffs/" + UUID.randomUUID() + "_" + file.getOriginalFilename()
                    : "tiffs/" + fileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(finalFileName)
                    .contentType("image/tiff")
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            log.info("Arquivo TIFF enviado com sucesso para: {}", finalFileName);
            return finalFileName;

        } catch (Exception e) {
            log.error("Erro ao fazer upload do TIFF", e);
            throw new RuntimeException("Erro ao fazer upload do arquivo .tif na AWS S3", e);
        }
    }
}