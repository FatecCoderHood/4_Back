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

    public Area createArea(@Valid AreaDto areaDtO) {
        if (areaDtO.getTamanho() <= 0) {
            throw new MessageException("O tamanho da área deve ser maior que zero.");
        }

        Area area = new Area();
        area.setNome( areaDtO.getNome());
        area.setLocalizacao( areaDtO.getLocalizacao());
        area.setTamanho( areaDtO.getTamanho());
        area.setCultura( areaDtO.getCultura());
        area.setProdutividade( areaDtO.getProdutividade());

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
                .orElseThrow(() -> new MessageException("Área com ID " + id + " não encontrada."));
        area.setNome(areaDto.getNome());
        area.setLocalizacao(areaDto.getLocalizacao());
        area.setTamanho(areaDto.getTamanho());
        area.setCultura(areaDto.getCultura());
        area.setProdutividade(areaDto.getProdutividade());

        return areaRepository.save(area);
    }

    public void deleteArea(UUID id) {
        if (!areaRepository.existsById(id)) {
            throw new MessageException("Área com ID " + id + " não encontrada.");
        }
        areaRepository.deleteById(id);
    }
}