package coderhood.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class AwsS3UploadService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String uploadTiff(MultipartFile file, String fileName) {
        try {
            System.out.println("== RECEBENDO UPLOAD DE TIFF (AWS) ==");
            System.out.println("Original file name: " + file.getOriginalFilename());
            System.out.println("Custom file name (param): " + fileName);
            System.out.println("File size: " + file.getSize() + " bytes");

            String finalFileName = (fileName == null || fileName.isBlank())
                    ? file.getOriginalFilename()
                    : fileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(finalFileName)
                    .contentType("image/tiff")
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return "Upload realizado com sucesso para AWS S3: " + finalFileName;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao fazer upload do arquivo .tif na AWS S3", e);
        }
    }
}
