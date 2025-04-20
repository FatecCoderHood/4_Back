package coderhood.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class TalhaoCreateDto {
    @NotNull(message = "ID da área é obrigatório")
    private UUID areaId;

    @NotBlank(message = "GeoJSON do talhão é obrigatório")
    private String geojson;

    private String cultura;
    private Double produtividade;
}
