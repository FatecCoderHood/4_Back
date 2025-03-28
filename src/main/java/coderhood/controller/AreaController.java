package coderhood.controller;

import coderhood.dto.AreaDto;
import coderhood.model.Area;
import coderhood.service.AreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Operation(summary = "Criar área")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Área criada com sucesso", 
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = Area.class))),
        @ApiResponse(responseCode = "400", description = "Entrada inválida", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Area> createArea(@Valid @RequestBody AreaDto areaDTO) {
        Area area = areaService.createArea(areaDTO);
        return new ResponseEntity<>(area, HttpStatus.CREATED);
    }

    @Operation(summary = "Consultar área por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Área localizada com sucesso", 
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = Area.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Area>> getAreaById(@PathVariable UUID id) {
        Optional<Area> area = areaService.findAreaById(id);
        return ResponseEntity.ok(area);
    }

    @Operation(summary = "Consultar todas as áreas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Áreas localizadas com sucesso", 
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = Area.class)))
    })
    @GetMapping
    public ResponseEntity<Iterable<Area>> getAllAreas() {
        Iterable<Area> areas = areaService.findAllAreas();
        return ResponseEntity.ok(areas);
    }

    @Operation(summary = "Editar área por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Área editada com sucesso", 
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = Area.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Area> updateArea(@PathVariable UUID id, @Valid @RequestBody AreaDto areaDto) {
        Area updatedArea = areaService.updateArea(id, areaDto);
        return ResponseEntity.ok(updatedArea);
    }

    @Operation(summary = "Deletar área por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Área deletada com sucesso", 
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = Area.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArea(@PathVariable UUID id) {
        areaService.deleteArea(id);
        return ResponseEntity.noContent().build();
    }
}