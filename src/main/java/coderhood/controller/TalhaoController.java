package coderhood.controller;

import coderhood.dto.TalhaoCreateDto;
import coderhood.dto.TalhaoDto;
import coderhood.exception.MessageException;
import coderhood.model.Area;
import coderhood.model.Talhao;
import coderhood.repository.AreaRepository;
import coderhood.repository.TalhaoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/talhoes")
public class TalhaoController {

    @Autowired
    private TalhaoRepository talhaoRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/area/{areaId}")
    public List<TalhaoDto> listarTalhoesPorArea(@PathVariable UUID areaId) {
        List<Talhao> talhoes = talhaoRepository.findByAreaId(areaId);
        return talhoes.stream().map(t -> new TalhaoDto(
                t.getId(),
                t.getGeojson(),
                t.getCultura(),
                t.getProdutividade()
        )).collect(Collectors.toList());
    }

    @GetMapping("/area/{areaId}/geojson")
    public ObjectNode exportarGeoJsonArea(@PathVariable UUID areaId) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new RuntimeException("Área não encontrada: " + areaId));

        List<Talhao> talhoes = talhaoRepository.findByAreaId(areaId);

        ObjectNode featureCollection = objectMapper.createObjectNode();
        featureCollection.put("type", "FeatureCollection");

        ArrayNode featuresArray = objectMapper.createArrayNode();

        for (Talhao talhao : talhoes) {
            try {
                featuresArray.add(objectMapper.readTree(talhao.getGeojson()));
            } catch (Exception e) {
                throw new RuntimeException("Erro ao processar GeoJSON de um talhão: " + e.getMessage());
            }
        }

        featureCollection.set("features", featuresArray);
        return featureCollection;
    }
    @PostMapping
    public TalhaoDto criarTalhao(@RequestBody @Valid TalhaoCreateDto dto) {
        Area area = areaRepository.findById(dto.getAreaId())
                .orElseThrow(() -> new MessageException("Área não encontrada: " + dto.getAreaId()));

        try {
            objectMapper.readTree(dto.getGeojson());
        } catch (Exception e) {
            throw new MessageException("GeoJSON inválido: " + e.getMessage());
        }

        Talhao talhao = new Talhao();
        talhao.setArea(area);
        talhao.setGeojson(dto.getGeojson());
        talhao.setCultura(dto.getCultura());
        talhao.setProdutividade(dto.getProdutividade());

        Talhao salvo = talhaoRepository.save(talhao);

        return new TalhaoDto(salvo.getId(), salvo.getGeojson(), salvo.getCultura(), salvo.getProdutividade());
    }

    @PutMapping("/{id}")
    public TalhaoDto atualizarTalhao(@PathVariable UUID id, @RequestBody @Valid TalhaoCreateDto dto) {
        Talhao talhao = talhaoRepository.findById(id)
                .orElseThrow(() -> new MessageException("Talhão não encontrado: " + id));

        Area area = areaRepository.findById(dto.getAreaId())
                .orElseThrow(() -> new MessageException("Área não encontrada: " + dto.getAreaId()));

        try {
            objectMapper.readTree(dto.getGeojson());
        } catch (Exception e) {
            throw new MessageException("GeoJSON inválido: " + e.getMessage());
        }

        talhao.setArea(area);
        talhao.setGeojson(dto.getGeojson());
        talhao.setCultura(dto.getCultura());
        talhao.setProdutividade(dto.getProdutividade());

        Talhao atualizado = talhaoRepository.save(talhao);

        return new TalhaoDto(atualizado.getId(), atualizado.getGeojson(), atualizado.getCultura(), atualizado.getProdutividade());
    }

    @DeleteMapping("/{id}")
    public void deletarTalhao(@PathVariable UUID id) {
        if (!talhaoRepository.existsById(id)) {
            throw new MessageException("Talhão não encontrado: " + id);
        }
        talhaoRepository.deleteById(id);
    }
}
