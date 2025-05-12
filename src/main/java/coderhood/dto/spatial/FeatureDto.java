package coderhood.dto.spatial;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureDto
{
    @JsonProperty("type")
    private String type;

    @JsonProperty("geometry")
    private GeometryDataDto geometry;

    @JsonProperty("properties")
    private Map<String, Object> properties;
}