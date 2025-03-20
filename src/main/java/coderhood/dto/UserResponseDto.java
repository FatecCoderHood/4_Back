package coderhood.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class UserResponseDto {

    private UUID id;
    private String nome;
    private String email;
    private String tipoAcesso;

    public UserResponseDto(UUID id, String nome, String email, String tipoAcesso) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipoAcesso = tipoAcesso;
    }

}