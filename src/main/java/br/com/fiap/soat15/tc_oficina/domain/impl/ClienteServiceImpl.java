package br.com.fiap.soat15.tc_oficina.domain.impl;

import br.com.fiap.soat15.tc_oficina.domain.service.ClienteService;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.Cliente;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.ClienteRepository;
import br.com.fiap.soat15.tc_oficina.domain.validator.DocumentoValidator;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente criarCliente(Cliente cliente) {
        if (clienteRepository.findByCpfCnpj(cliente.getCpfCnpj()).isPresent()) {
            throw new IllegalArgumentException("CPF/CNPJ já cadastrado");
        }

        //valida cpf e cnpj (normal e alfanumerico)
        DocumentoValidator validator = new DocumentoValidator();
        if(!validator.isValid(cliente.getCpfCnpj())) {
            throw new IllegalArgumentException("CPF/CNPJ inválido");
        }

        return clienteRepository.save(cliente);
    }

    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado"));

        cliente.setNome(clienteAtualizado.getNome());
        cliente.setEmail(clienteAtualizado.getEmail());
        cliente.setTelefone(clienteAtualizado.getTelefone());
        cliente.setEndereco(clienteAtualizado.getEndereco());

        return clienteRepository.save(cliente);
    }

    public void deletarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado"));
        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }

    public Cliente obterClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado"));
    }

    public Cliente obterClientePorCpfCnpj(String cpfCnpj) {
        return clienteRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado"));
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }
}
