package coderhood.service;

import coderhood.dto.AreaDto;
import coderhood.exception.MessageException;
import coderhood.model.Area;
import coderhood.repository.AreaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class AreaService {

    @Autowired
    private AreaRepository areaRepository;

    public Area createArea(@Valid AreaDto areaDTO) {
        // Validação do GeoJSON
        if (areaDTO.getGeojson() == null || areaDTO.getGeojson().isEmpty()) {
            throw new MessageException("O conteúdo do GeoJSON é obrigatório.");
        }

        Area area = new Area();
        area.setNome(areaDTO.getNome());
        area.setLocalizacao(areaDTO.getLocalizacao());
        area.setGeojson(areaDTO.getGeojson()); 
        area.setCultura(areaDTO.getCultura());
        area.setProdutividade(areaDTO.getProdutividade());

        return areaRepository.save(area);
    }

    public Optional<Area> findAreaById(UUID id) {
        return areaRepository.findById(id);
    }

    public Iterable<Area> findAllAreas() {
        return areaRepository.findAll();
    }

    public Area updateArea(UUID id, @Valid AreaDto areaDto) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new MessageException("Área não encontrada: " + id));
        
        if (areaDto.getGeojson() != null && !areaDto.getGeojson().isEmpty()) {
            area.setGeojson(areaDto.getGeojson());
        }
        
        area.setNome(areaDto.getNome());
        area.setLocalizacao(areaDto.getLocalizacao());
        area.setCultura(areaDto.getCultura());
        area.setProdutividade(areaDto.getProdutividade());

        return areaRepository.save(area);
    }

    public void deleteArea(UUID id) {
        if (!areaRepository.existsById(id)) {
            throw new RuntimeException("Área não encontrada com ID: " + id);
        }
        areaRepository.deleteById(id); 
    }
}