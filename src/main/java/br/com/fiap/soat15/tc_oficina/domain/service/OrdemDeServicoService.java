package br.com.fiap.soat15.tc_oficina.domain.service;

import br.com.fiap.soat15.tc_oficina.domain.model.AdicionarItemDTO;
import br.com.fiap.soat15.tc_oficina.domain.model.AvancarStatusDTO;
import br.com.fiap.soat15.tc_oficina.domain.model.CriarOrdemDTO;
import br.com.fiap.soat15.tc_oficina.domain.model.OrdemDeServicoDTO;

import java.util.List;

public interface OrdemDeServicoService {

    OrdemDeServicoDTO criarOrdem(CriarOrdemDTO dto);

    OrdemDeServicoDTO obterOrdemPorId(Long id);

    List<OrdemDeServicoDTO> listarOrdens();

    List<OrdemDeServicoDTO> listarOrdensPorCliente(Long clienteId);

    OrdemDeServicoDTO avancarStatus(Long id, AvancarStatusDTO dto);

    OrdemDeServicoDTO adicionarItens(Long ordemId, AdicionarItemDTO dto);

    OrdemDeServicoDTO removerItem(Long ordemId, Long itemId);
}
