package coderhood.service;

import coderhood.dto.AreaRequestDto;
import coderhood.model.Area;
import coderhood.repository.AreaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeoJsonService {

    @Autowired
    private AreaRepository areaRepository;

    public List<String> importGeoJsonFile(MultipartFile file) throws IOException {
        List<String> errors = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(file.getInputStream());

        if (!rootNode.has("features")) {
            errors.add("Arquivo .geojson inválido: 'features' não encontrado.");
            return errors;
        }

        List<Area> areas = new ArrayList<>();

        for (JsonNode featureNode : rootNode.path("features")) {
            if (!featureNode.has("geometry") || !featureNode.has("properties")) {
                errors.add("Feature inválida: 'geometry' ou 'properties' não encontrados.");
                continue;
            }

            JsonNode properties = featureNode.path("properties");

            AreaRequestDto areaRequestDto = new AreaRequestDto();
            areaRequestDto.setNome(properties.path("nome").asText());
            areaRequestDto.setLocalizacao(properties.path("localizacao").asText());
            areaRequestDto.setTamanho(properties.path("tamanho").asDouble());
            areaRequestDto.setCultura(properties.path("cultura").asText());
            areaRequestDto.setProdutividade(properties.path("produtividade").asDouble());

            if (areaRequestDto.getTamanho() <= 0) {
                errors.add("Tamanho da área deve ser maior que zero para a área: " + areaRequestDto.getNome());
                continue;
            }

            Area area = new Area();
            area.setNome(areaRequestDto.getNome());
            area.setLocalizacao(areaRequestDto.getLocalizacao());
            area.setTamanho(areaRequestDto.getTamanho());
            area.setCultura(areaRequestDto.getCultura());
            area.setProdutividade(areaRequestDto.getProdutividade());

            areas.add(area);
        }

        if (!errors.isEmpty()) {
            return errors;
        }

        areaRepository.saveAll(areas);
        return List.of("Arquivo importado com sucesso.");
    }
}
