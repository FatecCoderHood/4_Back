package coderhood.controller;

import coderhood.dto.AreaRequestDto;
import coderhood.dto.AreaResponseDto;
import coderhood.service.AreaService;
import coderhood.service.GeoJsonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/areas")
public class AreaController {

    @Autowired
    private AreaService areaService;

    @Autowired
    private GeoJsonService geoJsonService;

    @PostMapping
    public ResponseEntity<AreaResponseDto> createArea(@Valid @RequestBody AreaRequestDto areaRequestDto) {
        AreaResponseDto areaResponseDto = areaService.createArea(areaRequestDto);
        return new ResponseEntity<>(areaResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaResponseDto> getAreaById(@PathVariable UUID id) {
        Optional<AreaResponseDto> areaResponseDto = areaService.findAreaById(id);
        return areaResponseDto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Iterable<AreaResponseDto>> getAllAreas() {
        Iterable<AreaResponseDto> areasResponseDto = areaService.findAllAreas();
        return ResponseEntity.ok(areasResponseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AreaResponseDto> updateArea(@PathVariable UUID id, @Valid @RequestBody AreaRequestDto areaRequestDto) {
        AreaResponseDto updatedAreaResponseDto = areaService.updateArea(id, areaRequestDto);
        return ResponseEntity.ok(updatedAreaResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArea(@PathVariable UUID id) {
        areaService.deleteArea(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<List<String>> importGeoJsonFile(@RequestParam("file") MultipartFile file) {
        try {
            List<String> response = geoJsonService.importGeoJsonFile(file);

            if (response.contains("Arquivo importado com sucesso.")) {
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(List.of("Erro ao processar o arquivo: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}