package coderhood.service;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class TiffUploadService {

    private final ObjectStorage objectStorage;

    @Value("${oracle.bucket.namespace}")
    private String namespace;

    @Value("${oracle.bucket.name}")
    private String bucketName;

    public String uploadTiff(MultipartFile file, String fileName) {
        try {
            System.out.println("== RECEBENDO UPLOAD DE TIFF ==");
            System.out.println("Original file name: " + file.getOriginalFilename());
            System.out.println("Custom file name (param): " + fileName);
            System.out.println("File size: " + file.getSize() + " bytes");

            // Fallback: usa o nome original se fileName estiver vazio
            String finalFileName = (fileName == null || fileName.isBlank())
                    ? file.getOriginalFilename()
                    : fileName;

            InputStream inputStream = file.getInputStream();

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucketName(bucketName)
                    .namespaceName(namespace)
                    .objectName(finalFileName)
                    .putObjectBody(inputStream)
                    .contentType("image/tiff")
                    .build();

            PutObjectResponse response = objectStorage.putObject(request);

            System.out.println("Upload realizado com sucesso. ETag: " + response.getETag());
            return "Upload realizado com sucesso. ETag: " + response.getETag();

        } catch (Exception e) {
            System.err.println("Erro ao fazer upload do arquivo .tif:");
            e.printStackTrace(); // Mostra o erro no log
            throw new RuntimeException("Erro ao fazer upload do arquivo .tif", e);
        }
    }
}
