package coderhood.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class GeoJsonService {

    @Autowired
    private ObjectMapper objectMapper;

    public String processGeoJson(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo GeoJSON vazio.");
        }

        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        validateGeoJson(content); 
        return content;
    }

    public void validateGeoJson(String geojson) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(geojson);
        
        if (!node.has("type")) {
            throw new IllegalArgumentException("GeoJSON inválido: falta o campo 'type'");
        }
        
        String type = node.get("type").asText();
        if (!"Feature".equalsIgnoreCase(type) && 
            !"Polygon".equalsIgnoreCase(type) && 
            !"MultiPolygon".equalsIgnoreCase(type) &&
            !"FeatureCollection".equalsIgnoreCase(type)) {
            throw new IllegalArgumentException("Tipo GeoJSON não suportado. Use Polygon, MultiPolygon ou Feature");
        }
    }
}