package br.com.fiap.soat15.tc_oficina.domain.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoDto {
    private UUID id;
    private String placa;
    private String marca;
    private String modelo;
    private Integer ano;
    // private UUID clienteId; // TODO: descomentar quando o CRUD de cliente estiver pronto
}
