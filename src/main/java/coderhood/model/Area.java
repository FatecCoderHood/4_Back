package coderhood.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
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

    @NotNull(message = "O nome é obrigatório")
    @Size(min = 3, max = 255, message = "O nome deve ter entre 3 e 255 caracteres")
    private String nome;

    @Size(max = 255, message = "A localização pode ter no máximo 255 caracteres")
    private String localizacao;

    @NotNull(message = "O tamanho é obrigatório")
    @PositiveOrZero(message = "O tamanho deve ser um valor positivo ou zero")
    private Double tamanho;

    @Size(max = 255, message = "A cultura pode ter no máximo 255 caracteres")
    private String cultura;

    @PositiveOrZero(message = "A produtividade deve ser um valor positivo ou zero")
    private Double produtividade;

}
