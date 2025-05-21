package coderhood.dto;

import java.util.List;
import coderhood.model.StatusArea;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TalhaoDto {
    private Long id;
    private String geojson;
    private Integer mnTl;
    private Double areaHaTl;
    private String solo;
    private String cultura;
    private String safra;
    private Double produtividadePorAno;
    private StatusArea status;
    private List<String> ervasDaninhas;
}