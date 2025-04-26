package coderhood.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AreaDto {
    @NotNull
    private String nome;
    private String estado;
    private String cidade;
}
