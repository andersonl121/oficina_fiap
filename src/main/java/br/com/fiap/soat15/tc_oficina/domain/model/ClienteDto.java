package br.com.fiap.soat15.tc_oficina.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClienteDto {
    private String nome;
    private String documento;
}
