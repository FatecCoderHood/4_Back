package coderhood.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AreaWizardDto {
    private String geojson;
    private String estado;
    private String cidade;
    private List<TalhaoDto> talhoes;
}