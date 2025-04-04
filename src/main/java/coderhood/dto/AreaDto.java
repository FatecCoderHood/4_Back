package coderhood.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AreaDto {

    @NotNull
    private String nome;

    private String localizacao;

    @NotNull(message = "O GeoJSON é obrigatório")
    private String geojson; 

    private String cultura;

    private Double produtividade;
}