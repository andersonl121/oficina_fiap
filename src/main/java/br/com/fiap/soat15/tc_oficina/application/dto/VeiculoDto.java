package br.com.fiap.soat15.tc_oficina.application.dto;

import br.com.fiap.soat15.tc_oficina.domain.validator.PlacaValida;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoDto {

    private Long id;

    @PlacaValida
    private String placa;
    private String marca;
    private String modelo;
    private Integer ano;

    @NotNull(message = "clienteId é um campo obrigatório")
    private Long clienteId;
}
