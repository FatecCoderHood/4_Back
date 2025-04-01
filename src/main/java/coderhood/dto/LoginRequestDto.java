package coderhood.dto;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
    @NotBlank(message = "Username cannot be blank") String email,
    @NotBlank(message = "Password cannot be blank") String senha
) {}
