package coderhood.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.UUID;

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
    private TipoAcesso tipoAcesso;

    @Column(nullable = false)
    private boolean ativo = true;

}