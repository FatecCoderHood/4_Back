package coderhood.dto.spatial;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeometryDataDto
{
    @JsonProperty("type")
    private String type;

    @JsonProperty("coordinates")
    private Object coordinates; // can be List or nested List depending on the geometry
}