package coderhood.controller;

import coderhood.dto.AreaDto;
import coderhood.model.Area;
import coderhood.service.AreaService;
import coderhood.service.GeoJsonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/areas")
public class AreaController {

    @Autowired
    private AreaService areaService;

    @Autowired
    private GeoJsonService geoJsonService;

    @Operation(summary = "Criar área via JSON")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Área criada com sucesso",
            content = @Content(schema = @Schema(implementation = Area.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<?> createArea(@Valid @RequestBody AreaDto areaDTO) {
        try {
            Area area = areaService.createArea(areaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(area);
        } catch (Exception e) {
            log.error("Erro ao criar área: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Criar área com upload de GeoJSON")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Área criada com sucesso",
            content = @Content(schema = @Schema(implementation = Area.class))),
        @ApiResponse(responseCode = "400", description = "Arquivo ou dados inválidos"),
        @ApiResponse(responseCode = "500", description = "Erro no processamento do arquivo")
    })
    @PostMapping("/upload")
    public ResponseEntity<?> createAreaWithGeoJson(
            @RequestParam String nome,
            @RequestParam(required = false) String localizacao,
            @RequestParam(required = false) String cultura,
            @RequestParam Double produtividade,
            @RequestParam MultipartFile geojsonFile) {

        log.info("Recebendo upload - Nome: {}, Tamanho do arquivo: {}", nome, geojsonFile.getSize());

        try {
            if (geojsonFile.isEmpty()) {
                throw new IllegalArgumentException("Arquivo GeoJSON não pode ser vazio!");
            }

            String geojsonContent = geoJsonService.processGeoJson(geojsonFile);

            AreaDto areaDTO = AreaDto.builder()
                .nome(nome)
                .localizacao(localizacao)
                .cultura(cultura)
                .produtividade(produtividade)
                .geojson(geojsonContent)
                .build();

            Area area = areaService.createArea(areaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(area);

        } catch (Exception e) {
            log.error("Erro no upload: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Consultar área por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Área encontrada",
            content = @Content(schema = @Schema(implementation = Area.class))),
        @ApiResponse(responseCode = "404", description = "Área não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getAreaById(@PathVariable UUID id) {
        Optional<Area> area = areaService.findAreaById(id);
        return area.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar todas as áreas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de áreas retornada",
            content = @Content(schema = @Schema(implementation = Area.class)))
    })
    @GetMapping
    public ResponseEntity<Iterable<Area>> getAllAreas() {
        return ResponseEntity.ok(areaService.findAllAreas());
    }

    @Operation(summary = "Atualizar área")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Área atualizada",
            content = @Content(schema = @Schema(implementation = Area.class))),
        @ApiResponse(responseCode = "404", description = "Área não encontrada")
    })
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateArea(@PathVariable UUID id, @Valid @RequestBody AreaDto areaDto) {
        try {
            Area updatedArea = areaService.updateArea(id, areaDto);
            return ResponseEntity.ok(updatedArea);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Deletar área")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Área deletada"),
        @ApiResponse(responseCode = "404", description = "Área não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArea(@PathVariable UUID id) {
    try {
        areaService.deleteArea(id); 
        return ResponseEntity.noContent().build();
    } catch (Exception e) {
        return ResponseEntity.notFound().build();
    }
   }
}

