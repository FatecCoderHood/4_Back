package coderhood.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_area")
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // <- IMPORTANTE para Long (ordem automática)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(length = 255)
    private String estado;

    @Column(length = 255)
    private String cidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusArea status = StatusArea.EM_ANALISE;

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Talhao> talhoes = new ArrayList<>();

    public void addTalhao(Talhao talhao) {
        talhoes.add(talhao);
        talhao.setArea(this);
    }

    public void clearTalhoes() {
        talhoes.forEach(talhao -> talhao.setArea(null));
        talhoes.clear();
    }
}
