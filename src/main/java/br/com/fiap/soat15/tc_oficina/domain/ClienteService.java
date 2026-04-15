package br.com.fiap.soat15.tc_oficina.domain;

import br.com.fiap.soat15.tc_oficina.domain.model.ClienteDto;

import java.util.List;

public interface ClienteService {

    List<ClienteDto> getCliente();
}
