package coderhood.controller;

import coderhood.dto.TiffFileDto;
import coderhood.service.TiffFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tiffs")
public class TiffFileController {

    private final TiffFileService tiffFileService;

    @PostMapping
    public ResponseEntity<?> uploadTiff(
            @RequestParam("file") MultipartFile file,
            @RequestParam("areaId") Long areaId,
            @RequestParam(value = "fileName", required = false) String fileName) {

        try {
            log.info("Iniciando upload de arquivo - Nome: {}, Tamanho: {} bytes, AreaID: {}",
                    file.getOriginalFilename(), file.getSize(), areaId);

            if (file.isEmpty()) {
                log.warn("Arquivo vazio recebido");
                return ResponseEntity.badRequest().body("Arquivo não pode estar vazio");
            }

            log.debug("Verificando tipo do arquivo: {}", file.getContentType());
            if (!file.getContentType().equals("image/tiff")) {
                log.warn("Tipo de arquivo inválido: {}", file.getContentType());
                return ResponseEntity.badRequest().body("Apenas arquivos TIFF são permitidos");
            }

            TiffFileDto response = tiffFileService.uploadTiff(file, areaId, fileName);
            log.info("Upload concluído com sucesso - ID: {}", response.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("ERRO NO UPLOAD - Motivo: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Erro durante o upload: " + e.getMessage());
        }
    }

    @GetMapping("/area/{areaId}")
    public ResponseEntity<?> getTiffsByArea(@PathVariable Long areaId) {
        try {
            log.info("Buscando TIFFs para área ID: {}", areaId);
            List<TiffFileDto> tiffs = tiffFileService.getTiffsByArea(areaId);

            if (tiffs.isEmpty()) {
                log.warn("Nenhum TIFF encontrado para área ID: {}", areaId);
            } else {
                log.debug("Encontrados {} TIFFs para área ID: {}", tiffs.size(), areaId);
            }

            return ResponseEntity.ok(tiffs);

        } catch (Exception e) {
            log.error("ERRO AO BUSCAR TIFFS - Motivo: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Erro ao buscar arquivos: " + e.getMessage());
        }
    }
}