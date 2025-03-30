package coderhood.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class GeoJsonDto {
    @NotBlank(message = "GeoJSON é obrigatório")
    private String geojson;
}