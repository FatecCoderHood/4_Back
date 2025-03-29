package coderhood.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class GeoJsonService {

    public String processGeoJson(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo GeoJSON vazio.");
        }

        return new String(file.getBytes(), StandardCharsets.UTF_8);
    }
}