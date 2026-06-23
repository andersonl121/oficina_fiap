package br.com.fiap.soat15.tc_oficina.application.dto;

import br.com.fiap.soat15.tc_oficina.domain.entity.StatusOS;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdemStatusDTO {
    private StatusOS status;
}

