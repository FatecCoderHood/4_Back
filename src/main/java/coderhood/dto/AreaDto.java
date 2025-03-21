package coderhood.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AreaDto {

    @NotNull
    private String nome;

    private String localizacao;

    @NotNull
    private Double tamanho;

    private String cultura;

    private Double produtividade;

}