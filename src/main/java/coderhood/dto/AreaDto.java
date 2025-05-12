package coderhood.dto;

import java.util.List;
import coderhood.model.StatusArea;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AreaDto {
    private Long id;  // Alterado de String para Long
    private String nome;
    private String estado;
    private String cidade;
    private StatusArea status;
    private List<TalhaoDto> talhoes;
}