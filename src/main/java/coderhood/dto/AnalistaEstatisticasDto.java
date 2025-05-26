package coderhood.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalistaEstatisticasDto {
    private Long id;
    private String nome;
    private String email;
    private Long quantidadeTalhoes;
    private Long horasAnalisadas; // Pode ser implementado futuramente
    private Integer numeroNomeAnalyst;
}
