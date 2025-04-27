package coderhood.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AreaWizardDto {
    private String geojson;
    private String estado;
    private String cidade;
    private List<TalhaoDto> talhoes;
}