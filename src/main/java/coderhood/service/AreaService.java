package coderhood.service;

import coderhood.model.Area;
import coderhood.repository.AreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AreaService {

    @Autowired
    private AreaRepository areaRepository;

    @Transactional
    public Area salvarArea(Area area) {
        return areaRepository.save(area);
    }

    @Transactional
    public List<Area> listarAreas() {
        return areaRepository.findAll();
    }

    @Transactional
    public Optional<Area> findById(UUID id) {
        return areaRepository.findById(id);
    }

    @Transactional
    public void deletarArea(UUID id) {
        areaRepository.deleteById(id);
    }
}