package coderhood.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import coderhood.exception.GeoJsonParsingException;
import coderhood.dto.geojson.FeatureCollectionDto;

public class GeoJsonParser
{
    public static FeatureCollectionDto fromGeoJson(String geoJson) throws GeoJsonParsingException 
    {
        ObjectMapper mapper = new ObjectMapper();

        try
        {
            return mapper.readValue(geoJson, FeatureCollectionDto.class);
        } catch (JsonProcessingException e)
        {
            throw new GeoJsonParsingException("Failed to parse GeoJSON", e);
        }
    }
}