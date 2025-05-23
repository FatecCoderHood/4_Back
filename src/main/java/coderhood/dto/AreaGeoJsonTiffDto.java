package coderhood.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Data
public class AreaGeoJsonTiffDto {
    private String nome;
    private String estado;
    private String cidade;
    private Map<String, Object> geojson;
    private Map<String, Object> ervasDaninhasGeojson;
    private Map<String, Double> produtividadePorAno;
    private MultipartFile tiffFile;
}