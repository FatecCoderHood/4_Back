package coderhood.controller;

import coderhood.dto.TalhaoStatusUpdateDto;
import coderhood.model.StatusArea;
import coderhood.service.AreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Status dos Talhões", description = "API para gerenciamento de status dos talhões")
@RestController
@RequestMapping("/areas/{areaId}/talhoes")
@RequiredArgsConstructor
public class TalhaoStatusController {

    private final AreaService areaService;

    @Operation(summary = "Atualizar status de um talhão")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status do talhão atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Área ou talhão não encontrado", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PutMapping("/{talhaoId}/status")
    public ResponseEntity<Void> updateTalhaoStatus(
            @PathVariable Long areaId,
            @PathVariable Long talhaoId,
            @RequestBody TalhaoStatusUpdateDto statusUpdateDto) {

        areaService.updateTalhaoStatus(areaId, talhaoId, statusUpdateDto.getStatus());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Obter status de um talhão")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status do talhão retornado com sucesso", content = @Content(schema = @Schema(implementation = StatusArea.class))),
            @ApiResponse(responseCode = "404", description = "Área ou talhão não encontrado", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/{talhaoId}/status")
    public ResponseEntity<StatusArea> getTalhaoStatus(
            @PathVariable Long areaId,
            @PathVariable Long talhaoId) {

        StatusArea status = areaService.getTalhaoStatus(areaId, talhaoId);
        return ResponseEntity.ok(status);
    }
}