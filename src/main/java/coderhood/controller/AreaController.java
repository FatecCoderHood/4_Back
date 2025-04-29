package coderhood.controller;

import coderhood.dto.*;
import coderhood.exception.MessageException;
import coderhood.model.Area;
import coderhood.service.AreaService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@RestController
@RequestMapping("/areas")
public class AreaController {

    @Autowired
    private AreaService areaService;

    @PostMapping
    @Operation(summary = "Cria uma nova área com ou sem GeoJSON")
    public ResponseEntity<?> createArea(@RequestBody AreaGeoJsonDto areaDTO) {
        log.info("Recebida requisição POST para criar nova área");
        try {
            log.debug("Validando DTO recebido");
            if (areaDTO.getNome() == null || areaDTO.getNome().isEmpty()) {
                throw new MessageException("Nome da área é obrigatório");
            }

            log.debug("Conteúdo do DTO recebido:");
            log.debug("Nome: {}", areaDTO.getNome());
            log.debug("Cidade: {}", areaDTO.getCidade());
            log.debug("Estado: {}", areaDTO.getEstado());
            log.debug("Tem GeoJSON principal: {}", areaDTO.getGeojson() != null);
            log.debug("Tem GeoJSON ervas daninhas: {}", areaDTO.getErvasDaninhasGeojson() != null);
            log.debug("Tem produtividade: {}", areaDTO.getProdutividadePorAno() != null);

            if (areaDTO.getProdutividadePorAno() != null) {
                areaDTO.getProdutividadePorAno()
                        .forEach((mnTl, prod) -> log.debug("Produtividade para talhão {}: {}", mnTl, prod));
            }

            Area area = areaService.createAreaWithGeoJson(areaDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(area);
        } catch (MessageException e) {
            log.error("Erro de validação: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Erro interno ao criar área", e);
            return ResponseEntity.internalServerError().body("Erro interno ao processar requisição");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateArea(@PathVariable Long id, @RequestBody AreaGeoJsonDto areaDto) {
        try {
            log.debug("Validando dados de atualização");
            if (areaDto.getNome() == null || areaDto.getNome().isEmpty()) {
                throw new MessageException("Nome da área é obrigatório");
            }

            Area updatedArea = areaService.updateAreaWithGeoJson(id, areaDto);
            log.info("Área ID {} atualizada com sucesso", id);
            return ResponseEntity.ok(updatedArea);
        } catch (MessageException e) {
            log.error("Erro ao atualizar área ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Erro interno ao atualizar área ID {}", id, e);
            return ResponseEntity.internalServerError().body("Erro interno ao atualizar área");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAreaById(@PathVariable Long id) {
        return areaService.findAreaById(id)
                .map(area -> {
                    log.debug("Área encontrada: {}", area);
                    return ResponseEntity.ok(area);
                })
                .orElseGet(() -> {
                    log.warn("Área ID {} não encontrada", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping
    public ResponseEntity<List<Area>> getAllAreas() {
        log.info("Recebida requisição GET para todas as áreas");
        List<Area> areas = areaService.findAllAreas();
        log.debug("Número de áreas encontradas: {}", areas.size());
        return ResponseEntity.ok(areas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArea(@PathVariable Long id) {
        log.info("Recebida requisição DELETE para área ID {}", id);
        try {
            areaService.deleteArea(id);
            log.info("Área ID {} deletada com sucesso", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao deletar área ID {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{areaId}/talhoes/{talhaoId}")
    public ResponseEntity<?> updateTalhao(
            @PathVariable Long areaId,
            @PathVariable Long talhaoId,
            @RequestBody TalhaoDto talhaoDto) {
        log.info("Recebida requisição PUT para atualizar talhão ID {} da área ID {}", talhaoId, areaId);
        try {
            var updatedTalhao = areaService.updateTalhao(areaId, talhaoId, talhaoDto);
            log.info("Talhão atualizado com sucesso");
            return ResponseEntity.ok(updatedTalhao);
        } catch (Exception e) {
            log.error("Erro ao atualizar talhão", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{areaId}/talhoes/{talhaoId}")
    public ResponseEntity<Void> deleteTalhao(
            @PathVariable Long areaId,
            @PathVariable Long talhaoId) {
        log.info("Recebida requisição DELETE para talhão ID {} da área ID {}", talhaoId, areaId);
        try {
            areaService.deleteTalhao(areaId, talhaoId);
            log.info("Talhão deletado com sucesso");
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao deletar talhão", e);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateFarmStatus(@PathVariable Long id, @RequestBody Map<String, String> statusRequest) {
        try {
            String status = statusRequest.get("status");
            Area updatedArea = areaService.updateFarmStatus(id, status);
            return ResponseEntity.ok(updatedArea);
        } catch (Exception e) {
            log.error("Erro ao atualizar status da fazenda: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}