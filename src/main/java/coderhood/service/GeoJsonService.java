package coderhood.service;

import coderhood.model.Area;
import coderhood.model.Talhao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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

    public List<Talhao> extractTalhoesFromGeoJson(String geojson, Area area) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(geojson);

        if (!root.has("type") || !"FeatureCollection".equalsIgnoreCase(root.get("type").asText())) {
            throw new IllegalArgumentException("GeoJSON deve ser do tipo FeatureCollection.");
        }

        List<Talhao> talhoes = new ArrayList<>();

        for (JsonNode feature : root.get("features")) {
            JsonNode geometry = feature.get("geometry");
            JsonNode properties = feature.get("properties");

            if (geometry == null || !geometry.has("type") || !geometry.has("coordinates")) {
                throw new IllegalArgumentException("Feature sem geometria válida.");
            }

            Talhao talhao = new Talhao();
            talhao.setArea(area);
            talhao.setGeojson(feature.toString()); // salva o Feature como está

            if (properties != null) {
                talhao.setCultura(properties.path("cultura").asText(null));
                if (properties.has("produtividade")) {
                    talhao.setProdutividade(properties.get("produtividade").asDouble());
                }
            }

            talhoes.add(talhao);
        }

        return talhoes;
    }

}