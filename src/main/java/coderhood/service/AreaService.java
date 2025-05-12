package coderhood.service;

import coderhood.dto.AreaDto;
import coderhood.dto.TalhaoDto;
import coderhood.dto.geojson.FeatureCollectionDto;
import coderhood.exception.GeoJsonParsingException;
import coderhood.exception.MessageException;
import coderhood.model.Area;
import coderhood.model.Talhao;
import coderhood.repository.AreaRepository;
import coderhood.utils.GeoJsonParser;
import coderhood.utils.GeometryMapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AreaService {

    @Autowired
    private AreaRepository areaRepository;

    public Area createArea(AreaDto areaDto) throws IOException, GeoJsonParsingException
    {
        Area area = new Area();

        area.setNome(areaDto.getNome());
        area.setEstado(areaDto.getEstado());
        area.setCidade(areaDto.getCidade());

        if (areaDto.getGeojson() != null && !areaDto.getGeojson().isEmpty())
        {
            ObjectMapper mapper = new ObjectMapper();
            String content = mapper.writeValueAsString(areaDto.getGeojson());
            // String content = new String(areaDto.getGeojson().getBytes(), StandardCharsets.UTF_8);
            List<Talhao> talhoes = processarGeoJson(GeoJsonParser.fromGeoJson(content));
            talhoes.forEach(area::addTalhao);
        }

        return areaRepository.save(area);
    }

    private List<Talhao> processarGeoJson(FeatureCollectionDto featureCollection)
    {
        try
        {
            return featureCollection.getFeatures().stream().map(feature -> 
            {
                Map<String, Object> properties = feature.getProperties();

                if (properties == null)
                {
                    throw new MessageException("Formato GeoJSON inválido - 'properties' não encontrado");
                }

                Talhao talhao = new Talhao();
                
                talhao.setMnTl(getIntegerProperty(properties, "MN_TL", "mnTl"));
                talhao.setAreaHaTl(getDoubleProperty(properties, "AREA_HA_TL", "areaHaTl"));
                talhao.setSolo(getStringProperty(properties, "SOLO", "solo"));
                talhao.setCultura(getStringProperty(properties, "CULTURA", "cultura"));
                talhao.setSafra(getStringProperty(properties, "SAFRA", "safra"));

                talhao.setGeojson(feature.toString()); // Salvando o feature completo como GeoJSON
                talhao.setGeometry(GeometryMapper.fromDto(feature.getGeometry()));

                log.info("RTX was here - AreaService::processarGeoSjon {}", talhao.getGeometry().toText());

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

    public Optional<Area> findAreaById(Long id) {
        return areaRepository.findById(id);
    }

    public List<Area> findAllAreas() {
        return areaRepository.findAll();
    }

    public Area updateArea(Long id, AreaDto areaDto) throws IOException, GeoJsonParsingException
    {
        Area area = getAreaOrThrow(id);

        area.setNome(areaDto.getNome());
        area.setEstado(areaDto.getEstado());
        area.setCidade(areaDto.getCidade());

        // if (areaDto.getGeojsonFile() != null && !areaDto.getGeojsonFile().isEmpty())
        // {
        //     area.clearTalhoes();

        //     String content = new String(areaDto.getGeojsonFile().getBytes(), StandardCharsets.UTF_8);
        //     List<Talhao> novosTalhoes = processarGeoJson(GeoJsonParser.fromGeoJson(content));
        //     novosTalhoes.forEach(area::addTalhao);
        // }

        return areaRepository.save(area);
    }

    public void deleteArea(Long id) {
        if (!areaRepository.existsById(id)) {
            throw new MessageException("Área não encontrada");
        }
        areaRepository.deleteById(id);
    }

    public Talhao updateTalhao(Long areaId, Long talhaoId, TalhaoDto talhaoDto) {
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

    public void deleteTalhao(Long areaId, Long talhaoId) {
        Area area = getAreaOrThrow(areaId);
        boolean removed = area.getTalhoes().removeIf(t -> t.getId().equals(talhaoId));
        if (!removed) {
            throw new MessageException("Talhão não encontrado");
        }
        areaRepository.save(area);
    }

    private Area getAreaOrThrow(Long id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new MessageException("Área não encontrada"));
    }

    public Map<String, Object> verificarProgresso(Long id) {
        Area area = getAreaOrThrow(id);
        Map<String, Object> progresso = new HashMap<>();
        progresso.put("etapa1Completa", area.getNome() != null && !area.getNome().isEmpty());
        progresso.put("etapa2Completa", !area.getTalhoes().isEmpty());
        return progresso;
    }
}
