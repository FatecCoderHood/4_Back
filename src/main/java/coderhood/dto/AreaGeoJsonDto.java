package coderhood.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AreaGeoJsonDto {
    private String nome;
    private String cidade;
    private String estado;
    private Map<String, Object> geojson;
    private Map<String, Object> ervasDaninhasGeojson; // Nome corrigido para manter consistência
    private Map<String, Double> produtividadePorAno; // Nome corrigido para manter consistência
}