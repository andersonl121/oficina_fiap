package br.com.fiap.soat15.tc_oficina.domain.service;

import br.com.fiap.soat15.tc_oficina.domain.model.ServicoDTO;

import java.util.List;

public interface ServicoService {

    ServicoDTO criarServico(ServicoDTO dto);

    ServicoDTO atualizarServico(Long id, ServicoDTO dto);

    void deletarServico(Long id);

    ServicoDTO obterServicoPorId(Long id);

    List<ServicoDTO> listarServicos();

    ServicoDTO recalcularTempoMedio(Long id, Integer tempoExecucaoMinutos);
}
