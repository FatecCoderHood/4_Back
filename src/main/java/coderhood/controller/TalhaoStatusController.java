package coderhood.controller;

import coderhood.dto.TalhaoStatusUpdateDto;
import coderhood.model.StatusArea;
import coderhood.model.User;
import coderhood.service.AreaService;
import coderhood.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Status dos Talhões", description = "API para gerenciamento de status dos talhões")
@RestController
@RequestMapping("/areas/{areaId}/talhoes")
@RequiredArgsConstructor
public class TalhaoStatusController {

    private final AreaService areaService;
    private final UserRepository userRepository;

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

        // Obter o usuário autenticado (se disponível)
        Long usuarioId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("Authentication: " + authentication);
        
        if (authentication != null && authentication.isAuthenticated() 
            && authentication.getPrincipal() instanceof UserDetails) {
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            
            // Buscar o usuário pelo email para obter o ID
            userRepository.findByEmail(email).ifPresent(user -> {
                // O usuarioId será atribuído dentro deste escopo
            });
            
            // Como não posso modificar a variável final dentro do lambda, vou fazer diferente
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                usuarioId = user.getId();
            }
        }

        areaService.updateTalhaoStatus(areaId, talhaoId, statusUpdateDto.getStatus(), usuarioId);
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

    @Operation(summary = "Obter estatísticas de aprovações de usuários")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/estatisticas/usuarios")
    public ResponseEntity<Map<String, Object>> obterEstatisticasUsuarios() {
        Map<String, Object> estatisticas = areaService.obterEstatisticasUsuarios();
        return ResponseEntity.ok(estatisticas);
    }

    @Operation(summary = "Obter histórico de aprovações de um usuário específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/estatisticas/usuarios/{usuarioId}/historico")
    public ResponseEntity<List<Map<String, Object>>> obterHistoricoUsuario(
            @PathVariable Long usuarioId) {
        
        List<Map<String, Object>> historico = areaService.obterHistoricoAprovacaoUsuario(usuarioId);
        return ResponseEntity.ok(historico);
    }

    @Operation(summary = "Obter relatório de produtividade dos aprovadores")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/estatisticas/produtividade")
    public ResponseEntity<Map<String, Object>> obterRelatorioProdutiviadeAprovadores() {
        Map<String, Object> relatorio = areaService.obterRelatorioProdutiviadeAprovadores();
        return ResponseEntity.ok(relatorio);
    }
}