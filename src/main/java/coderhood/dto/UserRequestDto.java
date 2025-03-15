package coderhood.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {

    @NotBlank
    private String nome;

    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6)
    private String senha;

    @NotNull
    private String tipoAcesso;
}