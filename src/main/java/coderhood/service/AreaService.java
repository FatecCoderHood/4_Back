package coderhood.service;

import coderhood.dto.AreaRequestDto;
import coderhood.dto.AreaResponseDto;
import coderhood.exception.MessageException;
import coderhood.model.Area;
import coderhood.repository.AreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Validated
@Service
public class AreaService {

    @Autowired
    private AreaRepository areaRepository;

    public AreaResponseDto createArea(AreaRequestDto areaRequestDto) {
        if (areaRequestDto.getTamanho() <= 0) {
            throw new MessageException("O tamanho da área deve ser maior que zero.");
        }

        Area area = new Area();
        area.setNome(areaRequestDto.getNome());
        area.setLocalizacao(areaRequestDto.getLocalizacao());
        area.setTamanho(areaRequestDto.getTamanho());
        area.setCultura(areaRequestDto.getCultura());
        area.setProdutividade(areaRequestDto.getProdutividade());

        Area savedArea = areaRepository.save(area);

        return AreaResponseDto.fromEntity(savedArea);
    }

    public Optional<AreaResponseDto> findAreaById(UUID id) {
        Optional<Area> area = areaRepository.findById(id);
        if (area.isEmpty()) {
            return Optional.empty();
        }

        AreaResponseDto areaResponseDto = AreaResponseDto.fromEntity(area.get());
        return Optional.of(areaResponseDto);
    }

    public Iterable<AreaResponseDto> findAllAreas() {
        Iterable<Area> areas = areaRepository.findAll();
        return ((List<Area>) areas).stream()
                .map(AreaResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public AreaResponseDto updateArea(UUID id, AreaRequestDto areaRequestDto) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new MessageException("Área com ID " + id + " não encontrada."));

        area.setNome(areaRequestDto.getNome());
        area.setLocalizacao(areaRequestDto.getLocalizacao());
        area.setTamanho(areaRequestDto.getTamanho());
        area.setCultura(areaRequestDto.getCultura());
        area.setProdutividade(areaRequestDto.getProdutividade());

        Area updatedArea = areaRepository.save(area);

        return AreaResponseDto.fromEntity(updatedArea);
    }

    public void deleteArea(UUID id) {
        if (!areaRepository.existsById(id)) {
            throw new MessageException("Área com ID " + id + " não encontrada.");
        }
        areaRepository.deleteById(id);
    }
}