package coderhood.service;

import coderhood.dto.AreaDto;
import coderhood.dto.GeoJsonDto;
import coderhood.exception.MessageException;
import coderhood.model.Area;
import coderhood.repository.AreaRepository;
import jakarta.validation.Valid;
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
        validateGeoJson(areaDTO.getGeojson());
        
        Area area = convertToEntity(areaDTO);
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
            existingArea.setGeojson(areaDto.getGeojson());
        }

        updateEntityFromDto(existingArea, areaDto);
        return areaRepository.save(existingArea);
    }

    public Area updateAreaGeoJson(UUID id, @Valid GeoJsonDto geoJsonDto) {
        validateGeoJson(geoJsonDto.getGeojson());
        
        Area area = getAreaOrThrow(id);
        area.setGeojson(geoJsonDto.getGeojson());
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

    private Area convertToEntity(AreaDto dto) {
        Area area = new Area();
        updateEntityFromDto(area, dto);
        area.setGeojson(dto.getGeojson());
        return area;
    }

    private void updateEntityFromDto(Area area, AreaDto dto) {
        area.setNome(dto.getNome());
        area.setLocalizacao(dto.getLocalizacao());
        area.setCultura(dto.getCultura());
        area.setProdutividade(dto.getProdutividade());
    }
}