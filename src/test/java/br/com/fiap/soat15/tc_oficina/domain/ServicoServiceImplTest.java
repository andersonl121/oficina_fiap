package br.com.fiap.soat15.tc_oficina.domain;

import br.com.fiap.soat15.tc_oficina.domain.impl.ServicoServiceImpl;
import br.com.fiap.soat15.tc_oficina.domain.model.ServicoDTO;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.Servico;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoServiceImplTest {

    @Mock
    private ServicoRepository servicoRepository;

    @InjectMocks
    private ServicoServiceImpl servicoService;

    private Long id;
    private Servico entidade;
    private ServicoDTO dto;

    @BeforeEach
    void setUp() {
        id = 1L;
        entidade = Servico.builder()
                .id(id)
                .nome("Troca de Óleo")
                .descricao("Troca de óleo e filtro")
                .preco(new BigDecimal("120.00"))
                .tempoEstimadoMinutos(30)
                .ativo(true)
                .build();

        dto = ServicoDTO.builder()
                .nome("Troca de Óleo")
                .descricao("Troca de óleo e filtro")
                .preco(new BigDecimal("120.00"))
                .tempoEstimadoMinutos(30)
                .build();
    }

    @Test
    @DisplayName("Deve listar serviços ativos e retornar DTOs")
    void deveListarServicosAtivos() {
        when(servicoRepository.findByAtivoTrue()).thenReturn(List.of(entidade));

        List<ServicoDTO> resultado = servicoService.listarServicos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Troca de Óleo");
        assertThat(resultado.get(0).getId()).isEqualTo(id);
        verify(servicoRepository).findByAtivoTrue();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há serviços ativos")
    void deveRetornarListaVazia() {
        when(servicoRepository.findByAtivoTrue()).thenReturn(List.of());

        assertThat(servicoService.listarServicos()).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar serviço por ID e retornar DTO")
    void deveBuscarServicoPorId() {
        when(servicoRepository.findById(id)).thenReturn(Optional.of(entidade));

        ServicoDTO resultado = servicoService.obterServicoPorId(id);

        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getNome()).isEqualTo("Troca de Óleo");
        assertThat(resultado.getPreco()).isEqualByComparingTo("120.00");
    }

    @Test
    @DisplayName("Deve lançar exceção quando serviço não encontrado por ID")
    void deveLancarExcecaoQuandoNaoEncontrado() {
        when(servicoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicoService.obterServicoPorId(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    @DisplayName("Deve criar serviço com sucesso e retornar DTO")
    void deveCriarServicoComSucesso() {
        when(servicoRepository.existsByNome(dto.getNome())).thenReturn(false);
        when(servicoRepository.save(any(Servico.class))).thenReturn(entidade);

        ServicoDTO resultado = servicoService.criarServico(dto);

        assertThat(resultado.getNome()).isEqualTo("Troca de Óleo");
        assertThat(resultado.getId()).isEqualTo(id);
        verify(servicoRepository).save(any(Servico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar serviço com nome duplicado")
    void deveLancarExcecaoComNomeDuplicado() {
        when(servicoRepository.existsByNome(dto.getNome())).thenReturn(true);

        assertThatThrownBy(() -> servicoService.criarServico(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Troca de Óleo");

        verify(servicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar serviço com sucesso e retornar DTO")
    void deveAtualizarServicoComSucesso() {
        ServicoDTO dtoAtualizado = ServicoDTO.builder()
                .nome("Troca de Óleo Sintético")
                .descricao("Óleo sintético de alta performance")
                .preco(new BigDecimal("180.00"))
                .tempoEstimadoMinutos(45)
                .build();

        Servico entidadeAtualizada = Servico.builder()
                .id(id).nome("Troca de Óleo Sintético")
                .preco(new BigDecimal("180.00"))
                .tempoEstimadoMinutos(45).ativo(true).build();

        when(servicoRepository.findById(id)).thenReturn(Optional.of(entidade));
        when(servicoRepository.existsByNome(dtoAtualizado.getNome())).thenReturn(false);
        when(servicoRepository.save(any(Servico.class))).thenReturn(entidadeAtualizada);

        ServicoDTO resultado = servicoService.atualizarServico(id, dtoAtualizado);

        assertThat(resultado.getNome()).isEqualTo("Troca de Óleo Sintético");
        assertThat(resultado.getPreco()).isEqualByComparingTo("180.00");
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com nome já existente")
    void deveLancarExcecaoAoAtualizarComNomeDuplicado() {
        ServicoDTO dtoAtualizado = ServicoDTO.builder()
                .nome("Alinhamento")
                .preco(new BigDecimal("80.00"))
                .tempoEstimadoMinutos(60)
                .build();

        when(servicoRepository.findById(id)).thenReturn(Optional.of(entidade));
        when(servicoRepository.existsByNome("Alinhamento")).thenReturn(true);

        assertThatThrownBy(() -> servicoService.atualizarServico(id, dtoAtualizado))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Alinhamento");

        verify(servicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar serviço inexistente")
    void deveLancarExcecaoAoAtualizarInexistente() {
        when(servicoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicoService.atualizarServico(id, dto))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("Deve deletar serviço com soft delete")
    void deveDeletarServicoComSoftDelete() {
        when(servicoRepository.findById(id)).thenReturn(Optional.of(entidade));

        servicoService.deletarServico(id);

        verify(servicoRepository).save(any(Servico.class));
        assertThat(entidade.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar serviço inexistente")
    void deveLancarExcecaoAoDeletarInexistente() {
        when(servicoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicoService.deletarServico(id))
                .isInstanceOf(NoSuchElementException.class);

        verify(servicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve definir tempo médio na primeira execução")
    void deveDefinirTempoMedioNaPrimeiraExecucao() {
        entidade.setTempoMedioExecucaoMinutos(null);
        when(servicoRepository.findById(id)).thenReturn(Optional.of(entidade));
        when(servicoRepository.save(any(Servico.class))).thenReturn(entidade);

        ServicoDTO resultado = servicoService.recalcularTempoMedio(id, 40);

        assertThat(resultado.getTempoMedioExecucaoMinutos()).isEqualTo(40);
        verify(servicoRepository).save(entidade);
    }

    @Test
    @DisplayName("Deve recalcular tempo médio com média simples")
    void deveRecalcularTempoMedioComMediaSimples() {
        entidade.setTempoMedioExecucaoMinutos(30);
        when(servicoRepository.findById(id)).thenReturn(Optional.of(entidade));
        when(servicoRepository.save(any(Servico.class))).thenReturn(entidade);

        ServicoDTO resultado = servicoService.recalcularTempoMedio(id, 50);

        assertThat(resultado.getTempoMedioExecucaoMinutos()).isEqualTo(40); // (30+50)/2
    }

    @Test
    @DisplayName("Deve lançar exceção ao recalcular tempo com valor inválido")
    void deveLancarExcecaoComTempoInvalido() {
        assertThatThrownBy(() -> servicoService.recalcularTempoMedio(id, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maior que zero");

        assertThatThrownBy(() -> servicoService.recalcularTempoMedio(id, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção ao recalcular tempo de serviço inexistente")
    void deveLancarExcecaoAoRecalcularInexistente() {
        when(servicoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicoService.recalcularTempoMedio(id, 30))
                .isInstanceOf(NoSuchElementException.class);
    }
}
