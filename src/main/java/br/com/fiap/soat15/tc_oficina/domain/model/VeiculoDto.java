package br.com.fiap.soat15.tc_oficina.domain.model;

import br.com.fiap.soat15.tc_oficina.domain.validator.PlacaValida;
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
    // private Long clienteId; // TODO: descomentar quando o CRUD de cliente estiver pronto
}
