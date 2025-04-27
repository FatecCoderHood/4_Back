package coderhood.controller;

import coderhood.dto.*;
import coderhood.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Usuários", description = "API para gerenciamento de usuários")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Criar novo usuário")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "E-mail já cadastrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        UserResponseDto createdUser = userService.createUser(userCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @Operation(summary = "Atualizar usuário existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "E-mail já está em uso",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateDto userUpdateDto) {
        UserResponseDto updatedUser = userService.updateUser(userId, userUpdateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Buscar usuário por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário encontrado",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID userId) {
        UserResponseDto user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Listar todos os usuários")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
        @ApiResponse(responseCode = "204", description = "Nenhum usuário cadastrado")
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return users.isEmpty() ? 
            ResponseEntity.noContent().build() : 
            ResponseEntity.ok(users);
    }

    @Operation(summary = "Excluir usuário")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // Classe interna para padronização de respostas de erro
    @Schema(description = "Modelo padrão para respostas de erro")
    private static class ErrorResponse {
        @Schema(description = "Código do erro", example = "USER_NOT_FOUND")
        public String code;
        
        @Schema(description = "Mensagem descritiva do erro", example = "Usuário não encontrado")
        public String message;
        
        @Schema(description = "Timestamp do erro", example = "2023-01-01T12:00:00Z")
        public String timestamp;
    }
}