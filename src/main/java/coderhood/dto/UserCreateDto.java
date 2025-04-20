package coderhood.dto;

import coderhood.model.User.TipoAcesso;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class UserCreateDto {
    @NotBlank
    private String nome;
    
    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    @Size(min = 6)
    private String senha;
    
    @NotNull
    private TipoAcesso tipoAcesso;
}