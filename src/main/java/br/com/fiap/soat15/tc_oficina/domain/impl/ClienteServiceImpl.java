package br.com.fiap.soat15.tc_oficina.domain.impl;

import br.com.fiap.soat15.tc_oficina.domain.ClienteService;
import br.com.fiap.soat15.tc_oficina.domain.model.ClienteDto;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServiceImpl implements ClienteService {

    private ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<ClienteDto> getCliente() {
        //TODO
        return List.of();
    }
}
