package coderhood.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import coderhood.service.AwsS3UploadService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/upload")
public class UploadController {

    private final AwsS3UploadService awsS3UploadService;

    @PostMapping("/tiff")
    public ResponseEntity<String> uploadTiff(@RequestParam("file") MultipartFile file,
            @RequestParam(value = "fileName", required = false) String fileName) {
        String response = awsS3UploadService.uploadTiff(file, fileName);
        return ResponseEntity.ok(response);
    }
}
