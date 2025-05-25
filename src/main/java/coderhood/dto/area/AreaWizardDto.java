package coderhood.dto.area;

import lombok.*;

import java.util.List;

import coderhood.dto.TalhaoDto;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AreaWizardDto {
    private String geojson;
    private String estado;
    private String cidade;
    private List<TalhaoDto> talhoes;
}