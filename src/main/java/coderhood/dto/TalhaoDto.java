package coderhood.dto;

import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TalhaoDto {
    private Long id; // Alterado de UUID para Long
    private String geojson;
    private Integer mnTl;
    private Double areaHaTl;
    private String solo;
    private String cultura;
    private String safra;
    private Double produtividadePorAno;
    private List<String> ervasDaninhas;
}