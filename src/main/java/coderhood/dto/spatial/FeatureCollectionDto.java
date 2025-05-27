package coderhood.dto.spatial;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Setter;
import lombok.Getter;


@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureCollectionDto
{
    @JsonProperty("type")
    private String type;

    @JsonProperty("name")
    private String name;

    @JsonProperty("features")
    private List<FeatureDto> features;
}