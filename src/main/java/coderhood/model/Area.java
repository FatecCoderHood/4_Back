package coderhood.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "tb_area")
@EqualsAndHashCode(of = "id")
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Size(min = 3, max = 255)
    private String nome;

    @Size(max = 255)
    private String localizacao;

    @NotNull
    @Lob
    @Column(name = "geojson")
    private String geojson;

    @Size(max = 255)
    private String cultura;

    private Double produtividade;

    @Column(columnDefinition = "geometry")
    private Geometry geometria;

    // Caso queira garantir que os métodos existam sem depender do Lombok:
    public Geometry getGeometria() {
        return geometria;
    }

    public void setGeometria(Geometry geometria) {
        this.geometria = geometria;
    }
}
