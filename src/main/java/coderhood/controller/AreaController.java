package coderhood.controller;

import coderhood.dto.AreaDto;
import coderhood.model.Area;
import coderhood.service.AreaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/areas")
@CrossOrigin(origins = "http://localhost:3000")
public class AreaController {

    @Autowired
    private AreaService areaService;

    @PostMapping
    public ResponseEntity<Area> createArea(@Valid @RequestBody AreaDto areaDTO) {
        try {
            Area area = areaService.createArea(areaDTO);

            // Criando cabeçalho para garantir que a resposta seja JSON
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");

            return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(area);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Area>> getAreaById(@PathVariable UUID id) {
        Optional<Area> area = areaService.findAreaById(id);

        if (area.isPresent()) {
            return ResponseEntity.ok().header("Content-Type", "application/json").body(area);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<Iterable<Area>> getAllAreas() {
        Iterable<Area> areas = areaService.findAllAreas();
        return ResponseEntity.ok().header("Content-Type", "application/json").body(areas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Area> updateArea(@PathVariable UUID id, @Valid @RequestBody AreaDto areaDto) {
        try {
            Area updatedArea = areaService.updateArea(id, areaDto);
            return ResponseEntity.ok().header("Content-Type", "application/json").body(updatedArea);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArea(@PathVariable UUID id) {
        areaService.deleteArea(id);
        return ResponseEntity.noContent().build();
    }
}
