package br.com.fiap.soat15.tc_oficina.domain;

import br.com.fiap.soat15.tc_oficina.domain.impl.ClienteServiceImpl;
import br.com.fiap.soat15.tc_oficina.domain.impl.VeiculoServiceImpl;
import br.com.fiap.soat15.tc_oficina.domain.model.ClienteDTO;
import br.com.fiap.soat15.tc_oficina.domain.model.VeiculoDto;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.Cliente;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.VeiculoEntity;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.ClienteRepository;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Long id;
    private Cliente cliente;
    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        id = 1L;
        cliente = Cliente.builder()
                .id(id)
                .nome("João Silva")
                .cpfCnpj("64818919063")
                .email("joao@example.com")
                .telefone("11999999999")
                .endereco("Rua A, 123")
                .ativo(true)
                .build();
        clienteDTO = ClienteDTO.builder()
                .id(id)
                .nome("João Silva")
                .cpfCnpj("64818919063")
                .email("joao@example.com")
                .telefone("11999999999")
                .endereco("Rua A, 123")
                .ativo(true)
                .build();
    }

    @Test
    @DisplayName("Deve listar todos os clientes")
    void deveListarTodosVeiculos() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        List<Cliente> resultado = clienteService.listarClientes();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().getCpfCnpj()).isEqualTo("64818919063");
        verify(clienteRepository).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há clientes")
    void deveRetornarListaVaziaQuandoNaoHaVeiculos() {
        when(clienteRepository.findAll()).thenReturn(List.of());

        List<Cliente> resultado = clienteService.listarClientes();

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso")
    void deveBuscarVeiculoPorId() {
        when(clienteService.obterClientePorId(id)).thenReturn(cliente);

        Cliente resultado = clienteService.obterClientePorId(id);

        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getCpfCnpj()).isEqualTo("64818919063");
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não encontrado por ID")
    void deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        when(clienteService.obterClientePorId(id)).thenReturn(null);

        assertThatThrownBy(() -> clienteService.obterClientePorId(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    @DisplayName("Deve criar cliente com sucesso")
    void deveCriarVeiculoComSucesso() {
        when(clienteRepository.existsById(clienteDTO.getId())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Cliente resultado = clienteService.criarCliente(cliente);

        assertThat(resultado.getCpfCnpj()).isEqualTo("64818919063");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cliente com cpf/cnpj duplicado")
    void deveLancarExcecaoComPlacaDuplicada() {
        when(clienteRepository.existsByCpfCnpj(clienteDTO.getCpfCnpj())).thenReturn(true);

        assertThatThrownBy(() -> clienteService.criarCliente(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("64818919063");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void deveAtualizarVeiculoComSucesso() {
        ClienteDTO clienteDTOAtualizado = ClienteDTO.builder()
                .id(id)
                .nome("Jurandir Silva")
                .cpfCnpj("22139483057")
                .email("jurandir@example.com")
                .telefone("188889999")
                .endereco("Rua B12, 321")
                .ativo(true)
                .build();

        Cliente clienteAtualizada = Cliente.builder()
                .id(id)
                .nome("Jurandir Silva")
                .cpfCnpj("22139483057")
                .email("jurandir@example.com")
                .telefone("188889999")
                .endereco("Rua B12, 321")
                .ativo(true)
                .build();

        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteAtualizada);

        Cliente resultado = clienteService.atualizarCliente(id, clienteAtualizada);

        assertThat(resultado.getCpfCnpj()).isEqualTo("22139483057");
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar cliente inexistente")
    void deveLancarExcecaoAoAtualizarVeiculoInexistente() {
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.atualizarCliente(id, cliente))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("Deve deletar veículo com sucesso")
    void deveDeletarVeiculoComSucesso() {
        when(clienteRepository.existsById(id)).thenReturn(true);

        clienteService.deletarCliente(id);

        verify(clienteRepository).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar cliente inexistente")
    void deveLancarExcecaoAoDeletarVeiculoInexistente() {
        when(clienteRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> clienteService.deletarCliente(id))
                .isInstanceOf(NoSuchElementException.class);

        verify(clienteRepository, never()).deleteById(any());
    }
}

