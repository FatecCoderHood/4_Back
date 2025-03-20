package coderhood.dto;

import lombok.AllArgsConstructor;
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

}