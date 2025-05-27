package coderhood.utils;

import coderhood.dto.spatial.GeometryDataDto;
import org.locationtech.jts.geom.*;
import java.util.List;

public class GeometryMapper {

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    public static Geometry fromDto(GeometryDataDto dto) {
        if (dto == null) {
            return null;
        }

        switch (dto.getType()) {
            case "Point":
                return createPoint(dto);
            case "LineString":
                return createLineString(dto);
            case "Polygon":
                return createPolygon(dto);
            case "MultiPolygon":
                return createMultiPolygon(dto);
            default:
                throw new UnsupportedOperationException("Unsupported geometry type: " + dto.getType());
        }
    }

    private static Point createPoint(GeometryDataDto dto) {
        List<?> coords = (List<?>) dto.getCoordinates();
        double x = ((Number) coords.get(0)).doubleValue();
        double y = ((Number) coords.get(1)).doubleValue();
        Point point = geometryFactory.createPoint(new Coordinate(x, y));
        point.setSRID(4326);
        return point;

    }

    private static LineString createLineString(GeometryDataDto dto) {
        List<List<Number>> coords = (List<List<Number>>) dto.getCoordinates();
        Coordinate[] coordinates = coords.stream()
                .map(coord -> new Coordinate(coord.get(0).doubleValue(), coord.get(1).doubleValue()))
                .toArray(Coordinate[]::new);
        LineString lineString = geometryFactory.createLineString(coordinates);
        lineString.setSRID(4326);
        return lineString;
    }

    private static Polygon createPolygon(GeometryDataDto dto) {
        List<List<List<Number>>> coords = (List<List<List<Number>>>) dto.getCoordinates();
        List<List<Number>> exteriorRing = coords.get(0); // Outer boundary
        Coordinate[] exteriorCoords = exteriorRing.stream()
                .map(coord -> new Coordinate(coord.get(0).doubleValue(), coord.get(1).doubleValue()))
                .toArray(Coordinate[]::new);
        LinearRing shell = geometryFactory.createLinearRing(exteriorCoords);
        Polygon polygon = geometryFactory.createPolygon(shell);
        polygon.setSRID(4326);
        return polygon;
    }

    private static MultiPolygon createMultiPolygon(GeometryDataDto dto) {
        List<List<List<List<Number>>>> coords = (List<List<List<List<Number>>>>) dto.getCoordinates();

        Polygon[] polygons = coords.stream()
                .map(polygonCoords -> {
                    List<List<Number>> exteriorRing = polygonCoords.get(0); // Only exterior ring
                    Coordinate[] exteriorCoords = exteriorRing.stream()
                            .map(coord -> new Coordinate(coord.get(1).doubleValue(), coord.get(0).doubleValue()))
                            .toArray(Coordinate[]::new);
                    LinearRing shell = geometryFactory.createLinearRing(exteriorCoords);
                    return geometryFactory.createPolygon(shell);
                })
                .toArray(Polygon[]::new);

        MultiPolygon multiPolygon = geometryFactory.createMultiPolygon(polygons);
        multiPolygon.setSRID(4326);
        return multiPolygon;
    }
}
