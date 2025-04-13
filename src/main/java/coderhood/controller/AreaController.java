package coderhood.controller;

import coderhood.dto.AreaDto;
import coderhood.dto.GeoJsonDto;
import coderhood.model.Area;
import coderhood.service.AreaService;
import coderhood.service.GeoJsonService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.locationtech.jts.geom.Geometry;


import java.util.List;
import java.util.Map;
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

    @Autowired
    private ObjectMapper objectMapper;

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

            // Processar o conteúdo do GeoJSON
            String geojsonContent = geoJsonService.processGeoJson(geojsonFile);

            // Converter o GeoJSON em Geometry
            Geometry geometry = geoJsonService.converterParaGeometry(geojsonContent);

            // Criar a área com a geometria
            AreaDto areaDTO = AreaDto.builder()
                .nome(nome)
                .localizacao(localizacao)
                .cultura(cultura)
                .produtividade(produtividade)
                .geojson(geojsonContent)
                .geometria(geometry)  // Adicionando a geometria ao DTO
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
    public ResponseEntity<List<Area>> getAllAreas() {
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
            log.error("Erro ao atualizar área: ", e);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Atualizar GeoJSON de uma área")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "GeoJSON atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = Area.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Área não encontrada")
    })
    @PutMapping("/{id}/geojson")
    public ResponseEntity<?> updateAreaGeoJson(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> payload) {
        
        try {
            if (!payload.containsKey("geojson")) {
                return ResponseEntity.badRequest().body("Campo 'geojson' é obrigatório");
            }

            Object geojsonObj = payload.get("geojson");
            String geojson;
            
            if (geojsonObj instanceof String) {
                geojson = (String) geojsonObj;
            } else {
                geojson = objectMapper.writeValueAsString(geojsonObj);
            }

            // Converter o GeoJSON para Geometry
            Geometry geometry = geoJsonService.converterParaGeometry(geojson);

            // Atualizar a área com a nova geometria
            Area updatedArea = areaService.updateAreaGeoJson(id, new GeoJsonDto(geojson), geometry);
            return ResponseEntity.ok(updatedArea);
            
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("GeoJSON inválido: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao atualizar GeoJSON: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao processar GeoJSON");
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
            log.error("Erro ao deletar área: ", e);
            return ResponseEntity.notFound().build();
        }
    }
}
