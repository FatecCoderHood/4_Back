package coderhood.service;

import coderhood.dto.TiffFileDto;
import coderhood.model.Area;
import coderhood.model.TiffFile;
import coderhood.repository.AreaRepository;
import coderhood.repository.TiffFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TiffFileService {
    private final S3Client s3Client;
    private final TiffFileRepository tiffFileRepository;
    private final AreaRepository areaRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public TiffFileDto uploadTiff(MultipartFile file, Long areaId, String customFileName) {
        try {
            log.debug("Verificando existência da área ID: {}", areaId);
            Area area = areaRepository.findById(areaId)
                    .orElseThrow(() -> {
                        log.error("Área não encontrada - ID: {}", areaId);
                        return new RuntimeException("Área não encontrada");
                    });

            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String s3Key = "tiffs/" + areaId + "/" +
                    (customFileName != null ? customFileName + fileExtension : UUID.randomUUID() + fileExtension);

            log.info("Preparando upload para S3 - Bucket: {}, Key: {}", bucketName, s3Key);
            log.debug("Detalhes do arquivo - Tipo: {}, Tamanho: {} bytes",
                    file.getContentType(), file.getSize());

            // Upload para o S3
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            log.debug("Upload para S3 concluído - Key: {}", s3Key);

            // Salva metadados
            TiffFile tiffFile = TiffFile.builder()
                    .fileName(originalFileName)
                    .s3Key(s3Key)
                    .s3Bucket(bucketName)
                    .area(area)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .build();

            TiffFile savedFile = tiffFileRepository.save(tiffFile);
            log.info("Metadados salvos no banco - ID: {}", savedFile.getId());

            // Gera URL pública
            String publicUrl = generatePublicUrl(s3Key);
            log.debug("URL pública gerada: {}", publicUrl);

            return mapToDto(savedFile, publicUrl);

        } catch (Exception e) {
            log.error("ERRO NO SERVICE - Motivo: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao processar arquivo TIFF: " + e.getMessage(), e);
        }
    }

    private String generatePublicUrl(String s3Key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName, region.trim(), s3Key);
    }

    private TiffFileDto mapToDto(TiffFile tiffFile, String publicUrl) {
        return TiffFileDto.builder()
                .id(tiffFile.getId())
                .fileName(tiffFile.getFileName())
                .s3Key(tiffFile.getS3Key())
                .areaId(tiffFile.getArea().getId())
                .areaNome(tiffFile.getArea().getNome())
                .contentType(tiffFile.getContentType())
                .fileSize(tiffFile.getFileSize())
                .url(publicUrl)
                .build();
    }

    public List<TiffFileDto> getTiffsByArea(Long areaId) {
        log.debug("Buscando TIFFs no banco para área ID: {}", areaId);
        return tiffFileRepository.findByAreaId(areaId).stream()
                .map(tiff -> {
                    String publicUrl = generatePublicUrl(tiff.getS3Key());
                    return mapToDto(tiff, publicUrl);
                })
                .toList();
    }
}