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

    public String toString()
    {
        Integer mnTl = Integer.parseInt(properties.getOrDefault("MN_TL", properties.get("mnTl")).toString());
        String areaHaTl = properties.getOrDefault("AREA_HA_TL", properties.get("areaHaTl")).toString().replace(",", ".");
        String solo = properties.getOrDefault("SOLO", properties.get("solo")).toString();
        String cultura = properties.getOrDefault("CULTURA", properties.get("cultura")).toString();
        String safra = properties.getOrDefault("SAFRA", properties.get("safra")).toString();
        String fazenda = properties.getOrDefault("FAZENDA", properties.get("fazenda")).toString();

        return String.format("{type=%s, properties={MN_TL=%d, AREA_HA_TL=%s, SOLO=%s, CULTURA=%s, SAFRA=%s, FAZENDA=%s}, %s}",
            type,
            mnTl,
            areaHaTl,
            solo,
            cultura,
            safra,
            fazenda,
            geometry.toString()
        );
    }
}