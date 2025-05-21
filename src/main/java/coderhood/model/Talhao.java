package coderhood.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Geometry;

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

    @Column(columnDefinition = "SDO_GEOMETRY")
    private Geometry geometry;

    private Double produtividadePorAno;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusArea status = StatusArea.EM_ABERTO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    @JsonBackReference
    private Area area;

    @ElementCollection
    @CollectionTable(name = "talhao_ervas", joinColumns = @JoinColumn(name = "talhao_id"))
    @Lob
    @Column(name = "erva_daninha")
    private List<String> ervasDaninhas = new ArrayList<>();
}