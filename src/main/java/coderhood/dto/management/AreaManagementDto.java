package coderhood.dto.management;

import java.util.List;

import coderhood.dto.TalhaoDto;
import coderhood.model.StatusArea;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AreaManagementDto {
    private Long id;
    private String nome;
    private String estado;
    private String cidade;
    private StatusArea status;
    private List<TalhaoDto> talhoes;
}