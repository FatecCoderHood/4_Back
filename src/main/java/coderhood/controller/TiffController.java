package coderhood.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import coderhood.service.TiffUploadService;

@RestController
@RequestMapping("/tiff")
@RequiredArgsConstructor
public class TiffController {

    private final TiffUploadService tiffUploadService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadTiff(@RequestParam("file") MultipartFile file,
            @RequestParam("fileName") String fileName) {
        // Logs para debug
        System.out.println("== RECEBENDO UPLOAD ==");
        System.out.println("file: " + (file != null ? file.getOriginalFilename() : "null"));
        System.out.println("fileName: " + fileName);

        if (!file.getOriginalFilename().endsWith(".tif") && !file.getOriginalFilename().endsWith(".tiff")) {
            return ResponseEntity.badRequest().body("Apenas arquivos .tif ou .tiff são permitidos.");
        }

        String response = tiffUploadService.uploadTiff(file, fileName);
        return ResponseEntity.ok(response);
    }
}
