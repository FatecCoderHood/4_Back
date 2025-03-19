package coderhood.controller;

import coderhood.model.Area;
import coderhood.service.AreaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/areas")
public class AreaController {

    @Autowired
    private AreaService areaService;

    @PostMapping
    public ResponseEntity<Area> criarArea(@Valid @RequestBody Area area) {
        Area areaSalva = areaService.salvarArea(area);
        return new ResponseEntity<>(areaSalva, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Area>> listarAreas() {
        List<Area> areas = areaService.listarAreas();
        return new ResponseEntity<>(areas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Area> buscarAreaPorId(@PathVariable UUID id) {
        Optional<Area> area = areaService.findById(id);
        return area.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarArea(@PathVariable UUID id) {
        areaService.deletarArea(id);
        return ResponseEntity.noContent().build();
    }
}