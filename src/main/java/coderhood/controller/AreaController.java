package coderhood.controller;

import coderhood.dto.*;
import coderhood.model.Area;
import coderhood.service.AreaService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@RestController
@RequestMapping("/areas")
public class AreaController {

    @Autowired
    private AreaService areaService;

    @PostMapping
    @Operation(summary = "Cria uma nova área com ou sem GeoJSON")
    public ResponseEntity<?> createArea(
        @RequestBody AreaDto areaDto)
        // @RequestPart("data") AreaDto areaDto,
        // @RequestParam(value = "file", required = false) MultipartFile geojsonFile)
    {
        
        try
        {
            if (areaDto.getGeojson() != null && !areaDto.getGeojson().isEmpty())
            {
                log.info("RTX was here - Nome: {}", areaDto.getNome());
                // areaDto.setGeojsonFile(geojsonFile);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(
                areaService.createArea(areaDto)
            );
        } catch (Exception e)
        {
            log.error("Erro ao criar área: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAreaById(@PathVariable UUID id) {
        return areaService.findAreaById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Area>> getAllAreas() {
        return ResponseEntity.ok(areaService.findAllAreas());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateArea(
        @PathVariable UUID id,
        @RequestBody AreaDto areaDto)
    {
        try
        {
            return ResponseEntity.ok(areaService.updateArea(id, areaDto));
        } catch (Exception e)
        {
            log.error("Erro ao atualizar área: ", e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArea(@PathVariable UUID id) {
        try {
            areaService.deleteArea(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{areaId}/talhoes/{talhaoId}")
    public ResponseEntity<?> updateTalhao(
            @PathVariable UUID areaId,
            @PathVariable UUID talhaoId,
            @RequestBody TalhaoDto talhaoDto) {
        try {
            var updatedTalhao = areaService.updateTalhao(areaId, talhaoId, talhaoDto);
            return ResponseEntity.ok(updatedTalhao);
        } catch (Exception e) {
            log.error("Erro ao atualizar talhão: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{areaId}/talhoes/{talhaoId}")
    public ResponseEntity<Void> deleteTalhao(
            @PathVariable UUID areaId,
            @PathVariable UUID talhaoId) {
        try {
            areaService.deleteTalhao(areaId, talhaoId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}