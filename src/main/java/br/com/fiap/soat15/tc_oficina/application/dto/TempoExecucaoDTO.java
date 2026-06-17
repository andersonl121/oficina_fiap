package br.com.fiap.soat15.tc_oficina.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TempoExecucaoDTO {
    private BigDecimal tempoMedio;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer quantidadeOrdens;
}
