package coderhood.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TalhaoDto {
    private UUID id;
    private String geojson;
    private Integer mnTl;
    private Double areaHaTl;
    private String solo;
    private String cultura;
    private String safra;
    private Double produtividadePorAno;
}