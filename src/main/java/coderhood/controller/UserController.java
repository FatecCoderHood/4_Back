package coderhood.controller;

import coderhood.dto.UserRequestDto;
import coderhood.dto.UserResponseDto;
import coderhood.model.Area;
import coderhood.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Criar usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso", 
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = Area.class))),
        @ApiResponse(responseCode = "400", description = "Entrada inválida", content = @Content)
    })
    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> userCreate(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDTO = userService.userCreate(userRequestDto);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Editar usuário por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário editado com sucesso", 
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = Area.class)))
    })
    @PutMapping("user/{id}")
    public String userUpdate(@PathVariable UUID id, @RequestBody UserRequestDto userRequestDto) {
        return userService.userUpdate(id, userRequestDto);
    }
}
