package br.com.fiap.soat15.tc_oficina.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    private String descricao;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    private BigDecimal preco;

    @NotNull(message = "Tempo estimado é obrigatório")
    @Min(value = 1, message = "Tempo estimado deve ser maior que zero")
    private Integer tempoEstimadoMinutos;

    private Integer tempoMedioExecucaoMinutos;

    private Boolean ativo;
}
