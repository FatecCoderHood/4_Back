package coderhood.controller;

import coderhood.dto.AreaDto;
import coderhood.model.Area;
import coderhood.service.AreaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/areas")
public class AreaController {

    @Autowired
    private AreaService areaService;

    @PostMapping
    public ResponseEntity<Area> createArea(@Valid @RequestBody AreaDto areaDTO) {
        Area area = areaService.createArea(areaDTO);
        return new ResponseEntity<>(area, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Area>> getAreaById(@PathVariable UUID id) {
        Optional<Area> area = areaService.findAreaById(id);
        return ResponseEntity.ok(area);
    }

    @GetMapping
    public ResponseEntity<Iterable<Area>> getAllAreas() {
        Iterable<Area> areas = areaService.findAllAreas();
        return ResponseEntity.ok(areas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Area> updateArea(@PathVariable UUID id, @Valid @RequestBody AreaDto areaDto) {
        Area updatedArea = areaService.updateArea(id, areaDto);
        return ResponseEntity.ok(updatedArea);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArea(@PathVariable UUID id) {
        areaService.deleteArea(id);
        return ResponseEntity.noContent().build();
    }
}