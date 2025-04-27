package coderhood.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor 
public class UserResponseDto {
    private Long id;        // Troquei de UUID para Long
    private String nome;
    private String email;
    private String tipoAcesso;
}
