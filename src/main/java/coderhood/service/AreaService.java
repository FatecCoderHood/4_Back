package coderhood.service;

import coderhood.dto.AreaDto;
import coderhood.dto.GeoJsonDto;
import coderhood.exception.MessageException;
import coderhood.model.Area;
import coderhood.repository.AreaRepository;
import jakarta.validation.Valid;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class AreaService {

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private GeoJsonService geoJsonService;

    public Area createArea(@Valid AreaDto areaDTO) {
        // Valida o GeoJSON
        validateGeoJson(areaDTO.getGeojson());
        
        // Converte o GeoJSON para Geometry
        Geometry geometria = converterGeoJsonParaGeometry(areaDTO.getGeojson());
        
        // Cria a entidade Area
        Area area = convertToEntity(areaDTO, geometria);
        return areaRepository.save(area);
    }

    public Optional<Area> findAreaById(UUID id) {
        return areaRepository.findById(id);
    }

    public List<Area> findAllAreas() {
        return (List<Area>) areaRepository.findAll();
    }

    public Area updateArea(UUID id, @Valid AreaDto areaDto) {
        Area existingArea = getAreaOrThrow(id);

        if (areaDto.getGeojson() != null && !areaDto.getGeojson().isEmpty()) {
            validateGeoJson(areaDto.getGeojson());
            // Atualiza a geometria
            Geometry geometria = converterGeoJsonParaGeometry(areaDto.getGeojson());
            existingArea.setGeometria(geometria);
        }

        updateEntityFromDto(existingArea, areaDto);
        return areaRepository.save(existingArea);
    }

    public Area updateAreaGeoJson(UUID id, @Valid GeoJsonDto geoJsonDto, Geometry geometry) {
        // Validação do GeoJSON
        validateGeoJson(geoJsonDto.getGeojson());
    
        // Carregar a área existente a partir do id
        Area area = areaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Área não encontrada"));
    
        // Atualizar o campo de geometria
        area.setGeometria(geometry);  // Usa o método setGeometria
    
        // Atualiza a geojson no modelo, se necessário
        area.setGeojson(geoJsonDto.getGeojson());
    
        // Salvar a área atualizada na base de dados
        return areaRepository.save(area);
    }
    
    public void deleteArea(UUID id) {
        if (!areaRepository.existsById(id)) {
            throw new MessageException("Área não encontrada com ID: " + id);
        }
        areaRepository.deleteById(id);
    }

    private Area getAreaOrThrow(UUID id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new MessageException("Área não encontrada: " + id));
    }

    private void validateGeoJson(String geojson) {
        if (geojson == null || geojson.isEmpty()) {
            throw new MessageException("O conteúdo do GeoJSON é obrigatório.");
        }
        try {
            geoJsonService.validateGeoJson(geojson);
        } catch (Exception e) {
            throw new MessageException("GeoJSON inválido: " + e.getMessage());
        }
    }

    private Geometry converterGeoJsonParaGeometry(String geojson) {
        try {
            return geoJsonService.converterParaGeometry(geojson);
        } catch (Exception e) {
            throw new MessageException("Erro ao converter GeoJSON para geometria: " + e.getMessage());
        }
    }

    private Area convertToEntity(AreaDto dto, Geometry geometria) {
        Area area = new Area();
        updateEntityFromDto(area, dto);
        area.setGeojson(dto.getGeojson());
        area.setGeometria(geometria);
        return area;
    }

    private void updateEntityFromDto(Area area, AreaDto dto) {
        area.setNome(dto.getNome());
        area.setLocalizacao(dto.getLocalizacao());
        area.setCultura(dto.getCultura());
        area.setProdutividade(dto.getProdutividade());
    }
}
