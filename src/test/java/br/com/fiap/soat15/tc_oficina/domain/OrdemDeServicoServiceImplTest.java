package br.com.fiap.soat15.tc_oficina.domain;

import br.com.fiap.soat15.tc_oficina.domain.impl.OrdemDeServicoServiceImpl;
import br.com.fiap.soat15.tc_oficina.domain.model.*;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.Cliente;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.ItemOS;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.OrdemDeServico;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.Servico;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.StatusOS;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.Veiculo;
import br.com.fiap.soat15.tc_oficina.infrastructure.exception.BusinessException;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.ClienteRepository;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.ItemOSRepository;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.OrdemDeServicoRepository;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.ServicoRepository;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdemDeServicoServiceImplTest {

    @Mock private OrdemDeServicoRepository ordemRepository;
    @Mock private ItemOSRepository itemOSRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private VeiculoRepository veiculoRepository;
    @Mock private ServicoRepository servicoRepository;

    @InjectMocks
    private OrdemDeServicoServiceImpl ordemService;

    private Cliente cliente;
    private Veiculo veiculo;
    private Servico servico;
    private OrdemDeServico ordem;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L).nome("João Silva").cpfCnpj("11144477735").ativo(true).build();

        veiculo = Veiculo.builder()
                .id(2L).placa("ABC1234").marca("Toyota").modelo("Corolla").ano(2020)
                .cliente(cliente).build();

        servico = Servico.builder()
                .id(3L).nome("Troca de Óleo").preco(new BigDecimal("120.00"))
                .tempoEstimadoMinutos(30).ativo(true).build();

        // OrdemDeServico não tem campo cliente — cliente vem via veiculo.getCliente()
        ordem = OrdemDeServico.builder()
                .id(10L).numero("OS-2025-0001")
                .veiculo(veiculo)
                .status(StatusOS.ABERTA)
                .dataAbertura(LocalDateTime.now())
                .descricaoProblema("Carro fazendo barulho")
                .valorTotal(BigDecimal.ZERO)
                .itens(new ArrayList<>())
                .build();
    }

    // --- criarOrdem ---

    @Test
    @DisplayName("Deve criar OS com sucesso")
    void deveCriarOrdemComSucesso() {
        CriarOrdemDTO dto = CriarOrdemDTO.builder()
                .clienteId(1L).veiculoId(2L).descricaoProblema("Barulho no motor").build();

        when(veiculoRepository.findById(2L)).thenReturn(Optional.of(veiculo));
        when(ordemRepository.count()).thenReturn(0L);
        when(ordemRepository.save(any())).thenReturn(ordem);

        OrdemDeServicoDTO resultado = ordemService.criarOrdem(dto);

        assertThat(resultado.getNumero()).isEqualTo("OS-2025-0001");
        assertThat(resultado.getStatus()).isEqualTo(StatusOS.ABERTA);
        assertThat(resultado.getClienteId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando veículo não encontrado ao criar OS")
    void deveLancarExcecaoVeiculoNaoEncontrado() {
        CriarOrdemDTO dto = CriarOrdemDTO.builder()
                .clienteId(1L).veiculoId(99L).descricaoProblema("Problema").build();

        when(veiculoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordemService.criarOrdem(dto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("99");

        verify(ordemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando veículo não pertence ao cliente")
    void deveLancarExcecaoVeiculoNaoPertenceAoCliente() {
        Cliente outroCliente = Cliente.builder().id(99L).nome("Outro").build();
        Veiculo veiculoDeOutro = Veiculo.builder()
                .id(2L).placa("XYZ9999").cliente(outroCliente).build();

        CriarOrdemDTO dto = CriarOrdemDTO.builder()
                .clienteId(1L).veiculoId(2L).descricaoProblema("Problema").build();

        when(veiculoRepository.findById(2L)).thenReturn(Optional.of(veiculoDeOutro));

        assertThatThrownBy(() -> ordemService.criarOrdem(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("não pertence ao cliente");
    }

    // --- obterOrdemPorId ---

    @Test
    @DisplayName("Deve buscar OS por ID")
    void deveBuscarOrdemPorId() {
        when(ordemRepository.findById(10L)).thenReturn(Optional.of(ordem));

        OrdemDeServicoDTO resultado = ordemService.obterOrdemPorId(10L);

        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getNumero()).isEqualTo("OS-2025-0001");
    }

    @Test
    @DisplayName("Deve lançar exceção quando OS não encontrada por ID")
    void deveLancarExcecaoOsNaoEncontrada() {
        when(ordemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordemService.obterOrdemPorId(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("99");
    }

    // --- listarOrdens ---

    @Test
    @DisplayName("Deve listar todas as ordens")
    void deveListarOrdens() {
        when(ordemRepository.findAll()).thenReturn(List.of(ordem));

        List<OrdemDeServicoDTO> resultado = ordemService.listarOrdens();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNumero()).isEqualTo("OS-2025-0001");
    }

    // --- listarOrdensPorCliente ---

    @Test
    @DisplayName("Deve listar OS por cliente")
    void deveListarOrdensPorCliente() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        when(ordemRepository.findByVeiculoClienteId(1L)).thenReturn(List.of(ordem));

        List<OrdemDeServicoDTO> resultado = ordemService.listarOrdensPorCliente(1L);

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar OS de cliente inexistente")
    void deveLancarExcecaoClienteInexistenteAoListar() {
        when(clienteRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> ordemService.listarOrdensPorCliente(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("99");
    }

    // --- avancarStatus ---

    @Test
    @DisplayName("Deve avançar status de ABERTA para EM_DIAGNOSTICO")
    void deveAvancarStatusParaDiagnostico() {
        OrdemDeServico ordemSalva = OrdemDeServico.builder()
                .id(10L).numero("OS-2025-0001").veiculo(veiculo)
                .status(StatusOS.EM_DIAGNOSTICO).dataAbertura(LocalDateTime.now())
                .descricaoProblema("Barulho").valorTotal(BigDecimal.ZERO).itens(new ArrayList<>()).build();

        when(ordemRepository.findById(10L)).thenReturn(Optional.of(ordem));
        when(ordemRepository.save(any())).thenReturn(ordemSalva);

        OrdemDeServicoDTO resultado = ordemService.avancarStatus(10L,
                AvancarStatusDTO.builder().novoStatus(StatusOS.EM_DIAGNOSTICO).build());

        assertThat(resultado.getStatus()).isEqualTo(StatusOS.EM_DIAGNOSTICO);
    }

    @Test
    @DisplayName("Deve lançar exceção para transição inválida")
    void deveLancarExcecaoTransicaoInvalida() {
        when(ordemRepository.findById(10L)).thenReturn(Optional.of(ordem));

        assertThatThrownBy(() -> ordemService.avancarStatus(10L,
                AvancarStatusDTO.builder().novoStatus(StatusOS.CONCLUIDA).build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválida");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar alterar status de OS finalizada")
    void deveLancarExcecaoOsJaFinalizada() {
        ordem.setStatus(StatusOS.CONCLUIDA);
        when(ordemRepository.findById(10L)).thenReturn(Optional.of(ordem));

        assertThatThrownBy(() -> ordemService.avancarStatus(10L,
                AvancarStatusDTO.builder().novoStatus(StatusOS.CANCELADA).build()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("finalizada");
    }

    @Test
    @DisplayName("Deve preencher dataFechamento ao cancelar")
    void devePreencherDataFechamentoAoCancelar() {
        OrdemDeServico ordemCancelada = OrdemDeServico.builder()
                .id(10L).numero("OS-2025-0001").veiculo(veiculo)
                .status(StatusOS.CANCELADA).dataAbertura(LocalDateTime.now())
                .dataFechamento(LocalDateTime.now())
                .descricaoProblema("Barulho").valorTotal(BigDecimal.ZERO).itens(new ArrayList<>()).build();

        when(ordemRepository.findById(10L)).thenReturn(Optional.of(ordem));
        when(ordemRepository.save(any())).thenReturn(ordemCancelada);

        OrdemDeServicoDTO resultado = ordemService.avancarStatus(10L,
                AvancarStatusDTO.builder().novoStatus(StatusOS.CANCELADA).build());

        assertThat(resultado.getDataFechamento()).isNotNull();
    }

    @Test
    @DisplayName("Deve registrar dataInicioExecucao ao avançar para EM_EXECUCAO")
    void deveRegistrarDataInicioExecucao() {
        ordem.setStatus(StatusOS.APROVADA);

        OrdemDeServico ordemEmExecucao = OrdemDeServico.builder()
                .id(10L).numero("OS-2025-0001").veiculo(veiculo)
                .status(StatusOS.EM_EXECUCAO).dataAbertura(LocalDateTime.now())
                .dataInicioExecucao(LocalDateTime.now())
                .descricaoProblema("Barulho").valorTotal(BigDecimal.ZERO).itens(new ArrayList<>()).build();

        when(ordemRepository.findById(10L)).thenReturn(Optional.of(ordem));
        when(ordemRepository.save(any())).thenReturn(ordemEmExecucao);

        OrdemDeServicoDTO resultado = ordemService.avancarStatus(10L,
                AvancarStatusDTO.builder().novoStatus(StatusOS.EM_EXECUCAO).build());

        assertThat(resultado.getDataInicioExecucao()).isNotNull();
    }

    @Test
    @DisplayName("Deve recalcular tempo médio dos serviços ao concluir OS")
    void deveRecalcularTempoMedioAoConcluir() {
        ItemOS item = ItemOS.builder()
                .id(1L).ordemDeServico(ordem).servico(servico)
                .quantidade(1).precoUnitario(new BigDecimal("120.00"))
                .subtotal(new BigDecimal("120.00")).build();

        ordem.setStatus(StatusOS.EM_EXECUCAO);
        ordem.setDataInicioExecucao(LocalDateTime.now().minusMinutes(45));
        ordem.getItens().add(item);

        OrdemDeServico ordemConcluida = OrdemDeServico.builder()
                .id(10L).numero("OS-2025-0001").veiculo(veiculo)
                .status(StatusOS.CONCLUIDA).dataAbertura(LocalDateTime.now().minusHours(2))
                .dataInicioExecucao(LocalDateTime.now().minusMinutes(45))
                .dataFechamento(LocalDateTime.now())
                .descricaoProblema("Barulho").valorTotal(new BigDecimal("120.00"))
                .itens(new ArrayList<>(List.of(item))).build();

        when(ordemRepository.findById(10L)).thenReturn(Optional.of(ordem));
        when(ordemRepository.save(any())).thenReturn(ordemConcluida);
        when(servicoRepository.save(any())).thenReturn(servico);

        ordemService.avancarStatus(10L,
                AvancarStatusDTO.builder().novoStatus(StatusOS.CONCLUIDA).build());

        // Verifica que o serviço teve seu tempo médio atualizado
        verify(servicoRepository).save(servico);
        assertThat(servico.getTempoMedioExecucaoMinutos()).isNotNull().isGreaterThan(0);
    }

    // --- adicionarItens ---

    @Test
    @DisplayName("Deve adicionar um item à OS com sucesso")
    void deveAdicionarItemComSucesso() {
        ItemOS itemSalvo = ItemOS.builder()
                .id(1L).ordemDeServico(ordem).servico(servico)
                .quantidade(2).precoUnitario(new BigDecimal("120.00"))
                .subtotal(new BigDecimal("240.00")).build();

        OrdemDeServico ordemComItem = OrdemDeServico.builder()
                .id(10L).numero("OS-2025-0001").veiculo(veiculo)
                .status(StatusOS.ABERTA).dataAbertura(LocalDateTime.now())
                .descricaoProblema("Barulho").valorTotal(new BigDecimal("240.00"))
                .itens(new ArrayList<>(List.of(itemSalvo))).build();

        when(ordemRepository.findById(10L)).thenReturn(Optional.of(ordem));
        when(servicoRepository.findById(3L)).thenReturn(Optional.of(servico));
        when(ordemRepository.save(any())).thenReturn(ordemComItem);

        AdicionarItemDTO dto = AdicionarItemDTO.builder()
                .itens(List.of(AdicionarItemDTO.Item.builder().servicoId(3L).quantidade(2).build()))
                .build();

        OrdemDeServicoDTO resultado = ordemService.adicionarItens(10L, dto);

        assertThat(resultado.getItens()).hasSize(1);
        assertThat(resultado.getValorTotal()).isEqualByComparingTo("240.00");
    }

    @Test
    @DisplayName("Deve adicionar múltiplos itens à OS em uma única chamada")
    void deveAdicionarMultiplosItens() {
        Servico servico2 = Servico.builder()
                .id(4L).nome("Alinhamento").preco(new BigDecimal("80.00"))
                .tempoEstimadoMinutos(60).ativo(true).build();

        ItemOS item1 = ItemOS.builder().id(1L).ordemDeServico(ordem).servico(servico)
                .quantidade(1).precoUnitario(new BigDecimal("120.00")).subtotal(new BigDecimal("120.00")).build();
        ItemOS item2 = ItemOS.builder().id(2L).ordemDeServico(ordem).servico(servico2)
                .quantidade(1).precoUnitario(new BigDecimal("80.00")).subtotal(new BigDecimal("80.00")).build();

        OrdemDeServico ordemComItens = OrdemDeServico.builder()
                .id(10L).numero("OS-2025-0001").veiculo(veiculo)
                .status(StatusOS.ABERTA).dataAbertura(LocalDateTime.now())
                .descricaoProblema("Barulho").valorTotal(new BigDecimal("200.00"))
                .itens(new ArrayList<>(List.of(item1, item2))).build();

        when(ordemRepository.findById(10L)).thenReturn(Optional.of(ordem));
        when(servicoRepository.findById(3L)).thenReturn(Optional.of(servico));
        when(servicoRepository.findById(4L)).thenReturn(Optional.of(servico2));
        when(ordemRepository.save(any())).thenReturn(ordemComItens);

        AdicionarItemDTO dto = AdicionarItemDTO.builder()
                .itens(List.of(
                        AdicionarItemDTO.Item.builder().servicoId(3L).quantidade(1).build(),
                        AdicionarItemDTO.Item.builder().servicoId(4L).quantidade(1).build()
                ))
                .build();

        OrdemDeServicoDTO resultado = ordemService.adicionarItens(10L, dto);

        assertThat(resultado.getItens()).hasSize(2);
        assertThat(resultado.getValorTotal()).isEqualByComparingTo("200.00");
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar itens em OS com status inválido")
    void deveLancarExcecaoAoAdicionarItemStatusInvalido() {
        ordem.setStatus(StatusOS.APROVADA);
        when(ordemRepository.findById(10L)).thenReturn(Optional.of(ordem));

        AdicionarItemDTO dto = AdicionarItemDTO.builder()
                .itens(List.of(AdicionarItemDTO.Item.builder().servicoId(3L).quantidade(1).build()))
                .build();

        assertThatThrownBy(() -> ordemService.adicionarItens(10L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("APROVADA");
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar serviço inexistente")
    void deveLancarExcecaoServicoNaoEncontrado() {
        when(ordemRepository.findById(10L)).thenReturn(Optional.of(ordem));
        when(servicoRepository.findById(99L)).thenReturn(Optional.empty());

        AdicionarItemDTO dto = AdicionarItemDTO.builder()
                .itens(List.of(AdicionarItemDTO.Item.builder().servicoId(99L).quantidade(1).build()))
                .build();

        assertThatThrownBy(() -> ordemService.adicionarItens(10L, dto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("99");
    }

    // --- removerItem ---

    @Test
    @DisplayName("Deve remover item da OS com sucesso")
    void deveRemoverItemComSucesso() {
        ItemOS item = ItemOS.builder()
                .id(1L).ordemDeServico(ordem).servico(servico)
                .quantidade(1).precoUnitario(new BigDecimal("120.00"))
                .subtotal(new BigDecimal("120.00")).build();
        ordem.getItens().add(item);

        OrdemDeServico ordemSemItem = OrdemDeServico.builder()
                .id(10L).numero("OS-2025-0001").veiculo(veiculo)
                .status(StatusOS.ABERTA).dataAbertura(LocalDateTime.now())
                .descricaoProblema("Barulho").valorTotal(BigDecimal.ZERO)
                .itens(new ArrayList<>()).build();

        when(ordemRepository.findById(10L)).thenReturn(Optional.of(ordem));
        when(ordemRepository.save(any())).thenReturn(ordemSemItem);

        OrdemDeServicoDTO resultado = ordemService.removerItem(10L, 1L);

        assertThat(resultado.getItens()).isEmpty();
        assertThat(resultado.getValorTotal()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover item inexistente")
    void deveLancarExcecaoItemNaoEncontrado() {
        when(ordemRepository.findById(10L)).thenReturn(Optional.of(ordem));

        assertThatThrownBy(() -> ordemService.removerItem(10L, 99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover item em OS com status inválido")
    void deveLancarExcecaoAoRemoverItemStatusInvalido() {
        ordem.setStatus(StatusOS.EM_EXECUCAO);
        when(ordemRepository.findById(10L)).thenReturn(Optional.of(ordem));

        assertThatThrownBy(() -> ordemService.removerItem(10L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("EM_EXECUCAO");
    }

    @Test
    @DisplayName("Deve calcular tempo médio de execução do serviço corretamente")
    void deveCalcularTempoMedioCorretamente() {
        LocalDate dataInicial = LocalDate.of(2026, 4, 1);
        LocalDate dataFinal = LocalDate.of(2026, 4, 2);

        OrdemDeServico os1 = OrdemDeServico.builder()
                .dataInicioExecucao(LocalDateTime.of(2026, 4, 1, 10, 0))
                .dataFechamento(LocalDateTime.of(2026, 4, 1, 12, 0)) // 2h
                .status(StatusOS.CONCLUIDA)
                .build();

        OrdemDeServico os2 = OrdemDeServico.builder()
                .dataInicioExecucao(LocalDateTime.of(2026, 4, 1, 13, 0))
                .dataFechamento(LocalDateTime.of(2026, 4, 1, 16, 0)) // 3h
                .status(StatusOS.CONCLUIDA)
                .build();

        when(ordemRepository.findByDataExecucaoBetweenAndStatusEquals(any(), any(), eq(StatusOS.CONCLUIDA)))
                .thenReturn(List.of(os1, os2));

        TempoExecucaoDTO result = ordemService.listarTempoMedioPorPeriodo(dataInicial, dataFinal);

        assertEquals(2, result.getQuantidadeOrdens());
        assertEquals(new BigDecimal("2"), result.getTempoMedio()); // (2 + 3) / 2 = 2.5 → HALF_DOWN = 2
    }

    @Test
    @DisplayName("Deve calcular tempo médio de execução do serviço corretamente quando data final não for informada")
    void deveUsarDataInicialQuandoDataFinalForNull() {
        LocalDate dataInicial = LocalDate.of(2026, 4, 1);

        OrdemDeServico os1 = OrdemDeServico.builder()
                .dataInicioExecucao(LocalDateTime.of(2026, 4, 1, 10, 0))
                .dataFechamento(LocalDateTime.of(2026, 4, 1, 12, 0)) // 2h
                .status(StatusOS.CONCLUIDA)
                .build();

        OrdemDeServico os2 = OrdemDeServico.builder()
                .dataInicioExecucao(LocalDateTime.of(2026, 4, 1, 13, 0))
                .dataFechamento(LocalDateTime.of(2026, 4, 1, 16, 0)) // 3h
                .status(StatusOS.CONCLUIDA)
                .build();

        when(ordemRepository.findByDataExecucaoBetweenAndStatusEquals(any(), any(), eq(StatusOS.CONCLUIDA)))
                .thenReturn(List.of(os1, os2));

        TempoExecucaoDTO result = ordemService.listarTempoMedioPorPeriodo(dataInicial, null);

        assertEquals(2, result.getQuantidadeOrdens());
        assertEquals(new BigDecimal("2"), result.getTempoMedio()); // (2 + 3) / 2 = 2.5 → HALF_DOWN = 2

        verify(ordemRepository).findByDataExecucaoBetweenAndStatusEquals(
                eq(dataInicial.atStartOfDay()),
                eq(dataInicial.plusDays(1).atStartOfDay()),
                eq(StatusOS.CONCLUIDA)
        );
    }

    @Test
    @DisplayName("Deve calcular tempo médio de execução do serviço corretamente quando filtrado apenas uma ordem")
    void deveCalcularComUmaOrdem() {
        LocalDate data = LocalDate.of(2026, 4, 1);

        OrdemDeServico os = OrdemDeServico.builder()
                .dataInicioExecucao(LocalDateTime.of(2026, 4, 1, 10, 0))
                .dataFechamento(LocalDateTime.of(2026, 4, 1, 13, 0)) // 3h
                .status(StatusOS.CONCLUIDA)
                .build();

        when(ordemRepository.findByDataExecucaoBetweenAndStatusEquals(any(), any(), any()))
                .thenReturn(List.of(os));

        TempoExecucaoDTO result = ordemService.listarTempoMedioPorPeriodo(data, data);

        assertEquals(1, result.getQuantidadeOrdens());
        assertEquals(new BigDecimal("3"), result.getTempoMedio());
    }

    @Test
    @DisplayName("Deve calcular tempo médio de execução do serviço zerado quando registros não encontrados (lista vazia)")
    void deveRetornarZeroQuandoListaVazia() {
        LocalDate data = LocalDate.of(2026, 4, 1);

        when(ordemRepository.findByDataExecucaoBetweenAndStatusEquals(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        TempoExecucaoDTO result = ordemService.listarTempoMedioPorPeriodo(data, data);

        assertNotNull(result);
        assertEquals(0, result.getQuantidadeOrdens());
        assertEquals(BigDecimal.ZERO, result.getTempoMedio());
    }

    @Test
    @DisplayName("Deve calcular tempo médio de execução do serviço zerado quando registros não encontrados (lista nula)")
    void deveRetornarZeroQuandoListaNull() {
        LocalDate data = LocalDate.of(2026, 4, 1);

        when(ordemRepository.findByDataExecucaoBetweenAndStatusEquals(any(), any(), any()))
                .thenReturn(null);

        TempoExecucaoDTO result = ordemService.listarTempoMedioPorPeriodo(data, data);

        assertNotNull(result);
        assertEquals(0, result.getQuantidadeOrdens());
        assertEquals(BigDecimal.ZERO, result.getTempoMedio());
    }
}
