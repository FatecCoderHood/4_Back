package coderhood.service;

import coderhood.dto.AreaDto;
import coderhood.dto.AreaGeoJsonDto;
import coderhood.dto.TalhaoDto;
import coderhood.exception.MessageException;
import coderhood.model.Area;
import coderhood.model.Talhao;
import coderhood.repository.AreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AreaService {

    @Autowired
    private AreaRepository areaRepository;

    public Area createArea(AreaDto areaDTO) {
        Area area = new Area();
        area.setNome(areaDTO.getNome());
        area.setEstado(areaDTO.getEstado());
        area.setCidade(areaDTO.getCidade());
        return areaRepository.save(area);
    }

    public Area createAreaWithGeoJson(AreaGeoJsonDto areaDTO) {
        Area area = new Area();
        area.setNome(areaDTO.getNome());
        area.setEstado(areaDTO.getEstado());
        area.setCidade(areaDTO.getCidade());

        if (areaDTO.getGeojson() != null) {
            List<Talhao> talhoes = processarGeoJson(areaDTO.getGeojson());
            talhoes.forEach(area::addTalhao);
        }

        return areaRepository.save(area);
    }

    @SuppressWarnings("unchecked")
    private List<Talhao> processarGeoJson(Map<String, Object> geojson) {
        try {
            if (!(geojson.get("features") instanceof List)) {
                throw new MessageException("Formato GeoJSON inválido - 'features' deve ser uma lista");
            }

            List<Map<String, Object>> features = (List<Map<String, Object>>) geojson.get("features");

            return features.stream().map(feature -> {
                Map<String, Object> properties = (Map<String, Object>) feature.get("properties");
                if (properties == null) {
                    throw new MessageException("Formato GeoJSON inválido - 'properties' não encontrado");
                }

                Talhao talhao = new Talhao();
                talhao.setMnTl(getIntegerProperty(properties, "MN_TL", "mnTl"));
                talhao.setAreaHaTl(getDoubleProperty(properties, "AREA_HA_TL", "areaHaTl"));
                talhao.setSolo(getStringProperty(properties, "SOLO", "solo"));
                talhao.setCultura(getStringProperty(properties, "CULTURA", "cultura"));
                talhao.setSafra(getStringProperty(properties, "SAFRA", "safra"));

                talhao.setGeojson(feature.toString()); // Salvando o feature completo como GeoJSON
                return talhao;
            }).collect(Collectors.toList());

        } catch (ClassCastException e) {
            throw new MessageException("Erro de tipo no GeoJSON: " + e.getMessage());
        } catch (Exception e) {
            throw new MessageException("Erro ao processar GeoJSON: " + e.getMessage());
        }
    }

    private Integer getIntegerProperty(Map<String, Object> properties, String primaryKey, String secondaryKey) {
        Object value = properties.getOrDefault(primaryKey, properties.get(secondaryKey));
        return value != null ? Integer.parseInt(value.toString()) : null;
    }

    private Double getDoubleProperty(Map<String, Object> properties, String primaryKey, String secondaryKey) {
        Object value = properties.getOrDefault(primaryKey, properties.get(secondaryKey));
        return value != null ? Double.parseDouble(value.toString()) : null;
    }

    private String getStringProperty(Map<String, Object> properties, String primaryKey, String secondaryKey) {
        Object value = properties.getOrDefault(primaryKey, properties.get(secondaryKey));
        return value != null ? value.toString() : null;
    }

    public Optional<Area> findAreaById(UUID id) {
        return areaRepository.findById(id);
    }

    public List<Area> findAllAreas() {
        return areaRepository.findAll();
    }

    public Area updateArea(UUID id, AreaDto areaDto) {
        Area area = getAreaOrThrow(id);
        area.setNome(areaDto.getNome());
        area.setEstado(areaDto.getEstado());
        area.setCidade(areaDto.getCidade());
        return areaRepository.save(area);
    }

    public Area updateAreaWithGeoJson(UUID id, AreaGeoJsonDto areaDto) {
        Area area = getAreaOrThrow(id);
        area.setNome(areaDto.getNome());
        area.setEstado(areaDto.getEstado());
        area.setCidade(areaDto.getCidade());

        if (areaDto.getGeojson() != null) {
            area.clearTalhoes();
            List<Talhao> novosTalhoes = processarGeoJson(areaDto.getGeojson());
            novosTalhoes.forEach(area::addTalhao);
        }

        return areaRepository.save(area);
    }

    public void deleteArea(UUID id) {
        if (!areaRepository.existsById(id)) {
            throw new MessageException("Área não encontrada");
        }
        areaRepository.deleteById(id);
    }

    public Talhao updateTalhao(UUID areaId, UUID talhaoId, TalhaoDto talhaoDto) {
        Area area = getAreaOrThrow(areaId);
        Talhao talhao = area.getTalhoes().stream()
                .filter(t -> t.getId().equals(talhaoId))
                .findFirst()
                .orElseThrow(() -> new MessageException("Talhão não encontrado"));

        talhao.setMnTl(talhaoDto.getMnTl());
        talhao.setAreaHaTl(talhaoDto.getAreaHaTl());
        talhao.setSolo(talhaoDto.getSolo());
        talhao.setCultura(talhaoDto.getCultura());
        talhao.setSafra(talhaoDto.getSafra());

        return areaRepository.save(area).getTalhoes().stream()
                .filter(t -> t.getId().equals(talhaoId))
                .findFirst()
                .orElseThrow(() -> new MessageException("Erro ao atualizar talhão"));
    }

    public void deleteTalhao(UUID areaId, UUID talhaoId) {
        Area area = getAreaOrThrow(areaId);
        boolean removed = area.getTalhoes().removeIf(t -> t.getId().equals(talhaoId));
        if (!removed) {
            throw new MessageException("Talhão não encontrado");
        }
        areaRepository.save(area);
    }

    private Area getAreaOrThrow(UUID id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new MessageException("Área não encontrada"));
    }

    public Map<String, Object> verificarProgresso(UUID id) {
        Area area = getAreaOrThrow(id);
        Map<String, Object> progresso = new HashMap<>();
        progresso.put("etapa1Completa", area.getNome() != null && !area.getNome().isEmpty());
        progresso.put("etapa2Completa", !area.getTalhoes().isEmpty());
        return progresso;
    }
}
