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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private StatusArea status = StatusArea.EM_ABERTO;

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Talhao> talhoes = new ArrayList<>();

    public void addTalhao(Talhao talhao) {
        talhoes.add(talhao);
        talhao.setArea(this);
        atualizarStatusArea();
    }

    public void clearTalhoes() {
        talhoes.forEach(talhao -> talhao.setArea(null));
        talhoes.clear();
        atualizarStatusArea();
    }

    public void atualizarStatusArea() {
        if (talhoes.isEmpty()) {
            this.status = StatusArea.EM_ABERTO;
            return;
        }

        long aprovados = talhoes.stream().filter(t -> t.getStatus() == StatusArea.APROVADO).count();
        long recusados = talhoes.stream().filter(t -> t.getStatus() == StatusArea.RECUSADO).count();
        long emAberto = talhoes.stream().filter(t -> t.getStatus() == StatusArea.EM_ABERTO).count();
        long emAnalise = talhoes.stream().filter(t -> t.getStatus() == StatusArea.EM_ANALISE).count();

        if (aprovados == talhoes.size()) {
            this.status = StatusArea.APROVADO;
        } else if (recusados == talhoes.size()) {
            this.status = StatusArea.RECUSADO;
        } else if (emAberto == talhoes.size()) {
            this.status = StatusArea.EM_ABERTO;
        } else {
            this.status = StatusArea.EM_ANALISE;
        }
    }
}