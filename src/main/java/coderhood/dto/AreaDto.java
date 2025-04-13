package coderhood.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

@Getter
@Setter
@Builder
public class AreaDto {

    @NotNull
    private String nome;

    private String localizacao;

    @NotNull(message = "O GeoJSON é obrigatório")
    private String geojson;  // Esta parte permanece, pois o GeoJSON será enviado como String

    private String cultura;

    private Double produtividade;

    // Adicionando o campo Geometry, que representa a geometria associada ao GeoJSON
    private Geometry geometria;
}
