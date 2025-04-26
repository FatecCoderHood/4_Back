package coderhood.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import coderhood.dto.TalhaoDto;
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

    public String extractFazendaName(String geojson) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(geojson);
        JsonNode features = root.path("features");
        
        if (features.isArray() && features.size() > 0) {
            JsonNode firstFeature = features.get(0);
            JsonNode properties = firstFeature.path("properties");
            
            if (properties.has("FAZENDA")) {
                return properties.get("FAZENDA").asText();
            }
        }
        throw new IllegalArgumentException("GeoJSON não contém propriedade 'FAZENDA'");
    }

    public List<TalhaoDto> extractTalhoes(String geojson) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(geojson);
        JsonNode features = root.path("features");
        
        List<TalhaoDto> talhoes = new ArrayList<>();
        
        for (JsonNode feature : features) {
            JsonNode properties = feature.path("properties");
            JsonNode geometry = feature.path("geometry");
            
            TalhaoDto talhao = new TalhaoDto();
            talhao.setMnTl(properties.path("MN_TL").asInt());
            talhao.setAreaHaTl(properties.path("AREA_HA_TL").asDouble());
            talhao.setSolo(properties.path("SOLO").asText());
            talhao.setCultura(properties.path("CULTURA").asText());
            talhao.setSafra(properties.path("SAFRA").asText());
            talhao.setGeojson(geometry.toString());
            
            // Adicione produtividade se existir no GeoJSON
            if (properties.has("PRODUTIVIDADE")) {
                talhao.setProdutividadePorAno(properties.path("PRODUTIVIDADE").asDouble());
            }
                
            talhoes.add(talhao);
        }
        
        return talhoes;
    }
}