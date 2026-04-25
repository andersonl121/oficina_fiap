package br.com.fiap.soat15.tc_oficina.domain.service;

import br.com.fiap.soat15.tc_oficina.infrastructure.entity.Cliente;

import java.util.List;

public interface ClienteService {

    public Cliente criarCliente(Cliente cliente);
    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado);
    public void deletarCliente(Long id);
    public Cliente obterClientePorId(Long id);
    public Cliente obterClientePorCpfCnpj(String cpfCnpj);
    public List<Cliente> listarClientes();
}
