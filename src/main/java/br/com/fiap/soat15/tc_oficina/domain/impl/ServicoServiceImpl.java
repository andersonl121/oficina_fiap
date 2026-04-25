package br.com.fiap.soat15.tc_oficina.domain.impl;

import br.com.fiap.soat15.tc_oficina.domain.model.ServicoDTO;
import br.com.fiap.soat15.tc_oficina.domain.service.ServicoService;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.Servico;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ServicoServiceImpl implements ServicoService {

    private final ServicoRepository servicoRepository;

    @Override
    public ServicoDTO criarServico(ServicoDTO dto) {
        if (servicoRepository.existsByNome(dto.getNome())) {
            throw new IllegalArgumentException("Já existe um serviço com o nome: " + dto.getNome());
        }
        return toDTO(servicoRepository.save(toEntity(dto)));
    }

    @Override
    public ServicoDTO atualizarServico(Long id, ServicoDTO dto) {
        Servico servico = buscarEntidade(id);

        boolean nomeAlterado = !servico.getNome().equals(dto.getNome());
        if (nomeAlterado && servicoRepository.existsByNome(dto.getNome())) {
            throw new IllegalArgumentException("Já existe um serviço com o nome: " + dto.getNome());
        }

        servico.setNome(dto.getNome());
        servico.setDescricao(dto.getDescricao());
        servico.setPreco(dto.getPreco());
        servico.setTempoEstimadoMinutos(dto.getTempoEstimadoMinutos());

        return toDTO(servicoRepository.save(servico));
    }

    @Override
    public void deletarServico(Long id) {
        Servico servico = buscarEntidade(id);
        servico.setAtivo(false);
        servicoRepository.save(servico);
    }

    @Override
    public ServicoDTO obterServicoPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    @Override
    public List<ServicoDTO> listarServicos() {
        return servicoRepository.findByAtivoTrue()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public ServicoDTO recalcularTempoMedio(Long id, Integer tempoExecucaoMinutos) {
        if (tempoExecucaoMinutos == null || tempoExecucaoMinutos <= 0) {
            throw new IllegalArgumentException("Tempo de execução deve ser maior que zero");
        }

        Servico servico = buscarEntidade(id);

        if (servico.getTempoMedioExecucaoMinutos() == null) {
            servico.setTempoMedioExecucaoMinutos(tempoExecucaoMinutos);
        } else {
            servico.setTempoMedioExecucaoMinutos(
                    (servico.getTempoMedioExecucaoMinutos() + tempoExecucaoMinutos) / 2
            );
        }

        return toDTO(servicoRepository.save(servico));
    }

    private Servico buscarEntidade(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Serviço não encontrado: " + id));
    }

    private Servico toEntity(ServicoDTO dto) {
        return Servico.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .preco(dto.getPreco())
                .tempoEstimadoMinutos(dto.getTempoEstimadoMinutos())
                .ativo(true)
                .build();
    }

    private ServicoDTO toDTO(Servico servico) {
        return ServicoDTO.builder()
                .id(servico.getId())
                .nome(servico.getNome())
                .descricao(servico.getDescricao())
                .preco(servico.getPreco())
                .tempoEstimadoMinutos(servico.getTempoEstimadoMinutos())
                .tempoMedioExecucaoMinutos(servico.getTempoMedioExecucaoMinutos())
                .ativo(servico.getAtivo())
                .build();
    }
}
