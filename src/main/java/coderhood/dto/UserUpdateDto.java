package coderhood.dto;

import coderhood.model.User.TipoAcesso;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    @Email(message = "E-mail inválido")
    @NotBlank(message = "E-mail é obrigatório")
    private String email;
    
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha; 
    
    @NotNull(message = "Tipo de acesso é obrigatório")
    private TipoAcesso tipoAcesso;
}
