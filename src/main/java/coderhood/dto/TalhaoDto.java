package coderhood.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TalhaoDto {
    private UUID id;
    private String geojson;
    private String cultura;
    private Double produtividade;
}
