package br.com.fiap.soat15.tc_oficina.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItensEstoqueCadastro {
    private Long itemEstoqueId;
    private Integer quantidade;
}