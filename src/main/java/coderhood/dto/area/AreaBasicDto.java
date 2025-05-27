package coderhood.dto.area;

import coderhood.model.StatusArea;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AreaBasicDto
{
    private Long id;
    private String nome;
    private String estado;
    private String cidade;
    private StatusArea status = StatusArea.EM_ABERTO;
}