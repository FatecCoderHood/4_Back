package coderhood.dto;

import java.util.List;
import java.util.Map;

import coderhood.model.StatusArea;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AreaDto {
    private Long id;  // Alterado de String para Long
    private String nome;
    private String estado;
    private String cidade;
    private StatusArea status;
    private List<TalhaoDto> talhoes;
    private Map<String, Object> geojson;
    private Map<String, Object> ervasDaninhasGeojson; // Nome corrigido para manter consistência
    private Map<String, Double> produtividadePorAno; // Nome corrigido para manter consistência
}