package coderhood.model;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Geometry;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_talhao")
public class Talhao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
  
    @Lob
    @Column(name = "geojson")
    private String geojson;

    private Integer mnTl;
    private Double areaHaTl;
    private String solo;
    private String cultura;
    private String safra;

    // @Column(name = "geometry", columnDefinition = "SDO_GEOMETRY")
    // private Geometry geometry;
    @Column(name = "geometry", columnDefinition = "SDO_GEOMETRY")
    @JdbcTypeCode(SqlTypes.GEOMETRY)
    private Geometry geometry;

    private Double produtividadePorAno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    @JsonBackReference
    private Area area;
}
