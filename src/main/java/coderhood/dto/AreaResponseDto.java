package coderhood.dto;

import coderhood.model.Area;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AreaResponseDto {

    private String nome;
    private String localizacao;
    private Double tamanho;
    private String cultura;
    private Double produtividade;

    public AreaResponseDto(String nome, String localizacao, Double tamanho, String cultura, Double produtividade) {
        this.nome = nome;
        this.localizacao = localizacao;
        this.tamanho = tamanho;
        this.cultura = cultura;
        this.produtividade = produtividade;
    }

    public static AreaResponseDto fromEntity(Area area) {
        return new AreaResponseDto(
                area.getNome(),
                area.getLocalizacao(),
                area.getTamanho(),
                area.getCultura(),
                area.getProdutividade()
        );
    }
}
