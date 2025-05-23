package coderhood.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // <- aqui também, tudo alinhado
    private Long id;

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

    public enum RoleEnum {
        ADMINISTRATOR(TipoAcesso.ADMIN, "Administrador"),
        CONSULTANT(TipoAcesso.CONSULTOR, "Consultor"),
        ANALYST(TipoAcesso.ANALISTA, "Analista");
    
        private final TipoAcesso code;
        private final String description;
    
        RoleEnum(TipoAcesso code, String description) {
            this.code = code;
            this.description = description;
        }
    
        public TipoAcesso getCode() {
            return code;
        }
    
        public String getDescription() {
            return description;
        }
    
        public static String getRoleDescriptionByCode(TipoAcesso code) {
            for (RoleEnum role : RoleEnum.values()) {
                if (role.getCode() == code) {
                    return role.getDescription();
                }
            }
            return null;
        }
    }

    public String getName() {
        return this.nome;
    }

    public String getEmail() {
        return this.email;
    }

    public String getRole() {
        return RoleEnum.getRoleDescriptionByCode(this.tipoAcesso);
    }
}
