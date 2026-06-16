package br.com.fiap.soat15.tc_oficina.domain.service;

import br.com.fiap.soat15.tc_oficina.application.dto.ServicoDTO;

import java.util.List;

public interface ServicoService {

    ServicoDTO criarServico(ServicoDTO dto);

    ServicoDTO atualizarServico(Long id, ServicoDTO dto);

    void deletarServico(Long id);

    ServicoDTO obterServicoPorId(Long id);

    List<ServicoDTO> listarServicos();
}
