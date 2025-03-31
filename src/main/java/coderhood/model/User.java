package coderhood.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.UUID;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@Table(name = "tb_user")
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank
    @Column(name = "nome")
    private String nome;

    @NotBlank
    @Email
    @Column(name = "email")
    private String email;

    @NotBlank
    @Column(name = "senha")
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_acesso")
    private TipoAcesso tipoAcesso;

    public enum TipoAcesso {
        ADMIN,
        CONSULTOR,
        ANALISTA;

        public static List<String> getValues() {
            return Arrays.stream(values())
                .map(Enum::name)
                .collect(Collectors.toList());
        }
    }
}