package coderhood.dto;

import java.util.List;

import coderhood.model.StatusArea;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AreaDto {
    private String id;
    private String nome;
    private String estado;
    private String cidade;
    private StatusArea status;
    private List<TalhaoDto> talhoes;
}
