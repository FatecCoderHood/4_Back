package coderhood.model;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_talhao")
public class Talhao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // <- Long ID em ordem
    private Long id;

    @Lob
    @Column(name = "geojson")
    private String geojson;

    private Integer mnTl;
    private Double areaHaTl;
    private String solo;
    private String cultura;
    private String safra;
    private Double produtividadePorAno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    @JsonBackReference
    private Area area;
}
