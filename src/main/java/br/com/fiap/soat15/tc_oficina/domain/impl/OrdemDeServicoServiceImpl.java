package br.com.fiap.soat15.tc_oficina.domain.impl;

import br.com.fiap.soat15.tc_oficina.domain.model.*;
import br.com.fiap.soat15.tc_oficina.domain.service.OrdemDeServicoService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.EnumSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class OrdemDeServicoServiceImpl implements OrdemDeServicoService {

    private static final Set<StatusOS> STATUS_PERMITE_ITENS = EnumSet.of(StatusOS.ABERTA, StatusOS.EM_DIAGNOSTICO);
    private static final Set<StatusOS> STATUS_TERMINAIS = EnumSet.of(StatusOS.CONCLUIDA, StatusOS.CANCELADA);

    private final OrdemDeServicoRepository ordemRepository;
    private final ItemOSRepository itemOSRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final ServicoRepository servicoRepository;

    @Override
    @Transactional
    public OrdemDeServicoDTO criarOrdem(CriarOrdemDTO dto) {
        Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() -> new NoSuchElementException("Veículo não encontrado: " + dto.getVeiculoId()));

        if (!veiculo.getCliente().getId().equals(dto.getClienteId())) {
            throw new BusinessException("Veículo não pertence ao cliente informado");
        }

        OrdemDeServico ordem = OrdemDeServico.builder()
                .numero(gerarNumero())
                .veiculo(veiculo)
                .status(StatusOS.ABERTA)
                .dataAbertura(LocalDateTime.now())
                .descricaoProblema(dto.getDescricaoProblema())
                .observacoes(dto.getObservacoes())
                .valorTotal(BigDecimal.ZERO)
                .build();

        return toDTO(ordemRepository.save(ordem));
    }

    @Override
    @Transactional(readOnly = true)
    public OrdemDeServicoDTO obterOrdemPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemDeServicoDTO> listarOrdens() {
        return ordemRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemDeServicoDTO> listarOrdensPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new NoSuchElementException("Cliente não encontrado: " + clienteId);
        }
        return ordemRepository.findByVeiculoClienteId(clienteId).stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public TempoExecucaoDTO listarTempoMedioPorPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        dataFinal = ofNullable(dataFinal).orElse(dataInicial);

        LocalDateTime dataFinalEndOfDay = dataFinal.plusDays(1).atStartOfDay();
        LocalDateTime dataInicialStartOfDay = dataInicial.atStartOfDay();

        List<OrdemDeServico> ordens = ordemRepository.findByDataExecucaoBetweenAndStatusEquals(
                dataInicialStartOfDay, dataFinalEndOfDay, StatusOS.CONCLUIDA);

        Integer qtdOrdens = 0;

        BigDecimal averageTime = BigDecimal.ZERO;

        if (nonNull(ordens) && !ordens.isEmpty()) {
            BigDecimal timeExecutions = BigDecimal.valueOf(ordens.stream()
                    .mapToLong(o -> Duration.between(
                            o.getDataInicioExecucao(),
                            o.getDataFechamento()
                    ).toHours())
                    .sum());

            qtdOrdens = ordens.size();

            averageTime = timeExecutions.divide(
                    BigDecimal.valueOf(qtdOrdens), RoundingMode.HALF_DOWN);
        }

        return TempoExecucaoDTO.builder()
                .tempoMedio(averageTime)
                .quantidadeOrdens(qtdOrdens)
                .dataInicio(dataInicial)
                .dataFim(dataFinal)
                .build();
    }

    @Override
    @Transactional
    public OrdemDeServicoDTO avancarStatus(Long id, AvancarStatusDTO dto) {
        OrdemDeServico ordem = buscarEntidade(id);
        validarTransicao(ordem.getStatus(), dto.getNovoStatus());

        ordem.setStatus(dto.getNovoStatus());

        if (dto.getObservacoes() != null && !dto.getObservacoes().isBlank()) {
            ordem.setObservacoes(dto.getObservacoes());
        }

        if (dto.getNovoStatus() == StatusOS.EM_EXECUCAO) {
            ordem.setDataInicioExecucao(LocalDateTime.now());
        }

        if (dto.getNovoStatus() == StatusOS.CONCLUIDA) {
            ordem.setDataFechamento(LocalDateTime.now());
            recalcularTempoMedioServicos(ordem);
        } else if (dto.getNovoStatus() == StatusOS.CANCELADA) {
            ordem.setDataFechamento(LocalDateTime.now());
        }

        return toDTO(ordemRepository.save(ordem));
    }

    @Override
    @Transactional
    public OrdemDeServicoDTO adicionarItens(Long ordemId, AdicionarItemDTO dto) {
        OrdemDeServico ordem = buscarEntidade(ordemId);

        if (!STATUS_PERMITE_ITENS.contains(ordem.getStatus())) {
            throw new IllegalArgumentException(
                    "Não é possível adicionar itens à OS no status: " + ordem.getStatus());
        }

        for (AdicionarItemDTO.Item itemDTO : dto.getItens()) {
            Servico servico = servicoRepository.findById(itemDTO.getServicoId())
                    .orElseThrow(() -> new NoSuchElementException("Serviço não encontrado: " + itemDTO.getServicoId()));

            BigDecimal precoUnitario = servico.getPreco();
            BigDecimal subtotal = precoUnitario.multiply(BigDecimal.valueOf(itemDTO.getQuantidade()));

            ItemOS item = ItemOS.builder()
                    .ordemDeServico(ordem)
                    .servico(servico)
                    .quantidade(itemDTO.getQuantidade())
                    .precoUnitario(precoUnitario)
                    .subtotal(subtotal)
                    .build();

            ordem.getItens().add(item);
        }

        recalcularTotal(ordem);
        return toDTO(ordemRepository.save(ordem));
    }

    @Override
    @Transactional
    public OrdemDeServicoDTO removerItem(Long ordemId, Long itemId) {
        OrdemDeServico ordem = buscarEntidade(ordemId);

        if (!STATUS_PERMITE_ITENS.contains(ordem.getStatus())) {
            throw new IllegalArgumentException(
                    "Não é possível remover itens da OS no status: " + ordem.getStatus());
        }

        ItemOS item = ordem.getItens().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Item não encontrado: " + itemId));

        ordem.getItens().remove(item);
        recalcularTotal(ordem);

        return toDTO(ordemRepository.save(ordem));
    }

    private OrdemDeServico buscarEntidade(Long id) {
        return ordemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ordem de serviço não encontrada: " + id));
    }

    private void validarTransicao(StatusOS atual, StatusOS novo) {
        if (STATUS_TERMINAIS.contains(atual)) {
            throw new BusinessException(
                    "Não é possível alterar o status de uma OS já finalizada: " + atual);
        }

        boolean valido = switch (atual) {
            case ABERTA -> novo == StatusOS.EM_DIAGNOSTICO || novo == StatusOS.CANCELADA;
            case EM_DIAGNOSTICO -> novo == StatusOS.AGUARDANDO_APROVACAO || novo == StatusOS.CANCELADA;
            case AGUARDANDO_APROVACAO -> novo == StatusOS.APROVADA || novo == StatusOS.CANCELADA;
            case APROVADA -> novo == StatusOS.EM_EXECUCAO || novo == StatusOS.CANCELADA;
            case EM_EXECUCAO -> novo == StatusOS.CONCLUIDA;
            default -> false;
        };

        if (!valido) {
            throw new IllegalArgumentException(
                    "Transição de status inválida: " + atual + " → " + novo);
        }
    }

    private void recalcularTempoMedioServicos(OrdemDeServico ordem) {
        if (ordem.getDataInicioExecucao() == null || ordem.getItens().isEmpty()) {
            return;
        }

        Long duracaoMinutos = Duration.between(ordem.getDataInicioExecucao(), LocalDateTime.now()).toMinutes();
        if (duracaoMinutos <= 0) {
            return;
        }

        int tempoExecucao = duracaoMinutos.intValue();

        for (ItemOS item : ordem.getItens()) {
            Servico servico = item.getServico();
            if (servico.getTempoMedioExecucaoMinutos() == null) {
                servico.setTempoMedioExecucaoMinutos(tempoExecucao);
            } else {
                servico.setTempoMedioExecucaoMinutos(
                        (servico.getTempoMedioExecucaoMinutos() + tempoExecucao) / 2
                );
            }
            servicoRepository.save(servico);
        }
    }

    private void recalcularTotal(OrdemDeServico ordem) {
        BigDecimal total = ordem.getItens().stream()
                .map(ItemOS::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ordem.setValorTotal(total);
    }

    private String gerarNumero() {
        int ano = Year.now().getValue();
        long count = ordemRepository.count() + 1;
        return String.format("OS-%d-%04d", ano, count);
    }

    private OrdemDeServicoDTO toDTO(OrdemDeServico ordem) {
        List<ItemOSDTO> itensDTO = ordem.getItens().stream().map(this::toItemDTO).toList();

        return OrdemDeServicoDTO.builder()
                .id(ordem.getId())
                .numero(ordem.getNumero())
                .clienteId(ordem.getVeiculo().getCliente().getId())
                .clienteNome(ordem.getVeiculo().getCliente().getNome())
                .veiculoId(ordem.getVeiculo().getId())
                .veiculoPlaca(ordem.getVeiculo().getPlaca())
                .veiculoModelo(ordem.getVeiculo().getModelo())
                .status(ordem.getStatus())
                .dataAbertura(ordem.getDataAbertura())
                .dataInicioExecucao(ordem.getDataInicioExecucao())
                .dataFechamento(ordem.getDataFechamento())
                .descricaoProblema(ordem.getDescricaoProblema())
                .observacoes(ordem.getObservacoes())
                .valorTotal(ordem.getValorTotal())
                .itens(itensDTO)
                .build();
    }

    private ItemOSDTO toItemDTO(ItemOS item) {
        return ItemOSDTO.builder()
                .id(item.getId())
                .servicoId(item.getServico().getId())
                .servicoNome(item.getServico().getNome())
                .quantidade(item.getQuantidade())
                .precoUnitario(item.getPrecoUnitario())
                .subtotal(item.getSubtotal())
                .build();
    }
}
