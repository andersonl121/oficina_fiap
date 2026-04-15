package br.com.fiap.soat15.tc_oficina.application;

import br.com.fiap.soat15.tc_oficina.domain.ClienteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ClienteController {

    private ClienteService clienteService;
}
