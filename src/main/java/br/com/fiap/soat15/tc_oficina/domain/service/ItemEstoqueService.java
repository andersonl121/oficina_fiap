package br.com.fiap.soat15.tc_oficina.domain.service;

import br.com.fiap.soat15.tc_oficina.domain.model.ItemEstoqueDTO;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.ItemEstoque;

import java.util.List;

public interface ItemEstoqueService {

    ItemEstoqueDTO adicionarItem(ItemEstoqueDTO itemEstoque);
    ItemEstoque atualizarItem(Long id, ItemEstoqueDTO itemAtualizado);
    void deletarItem(Long id);
    ItemEstoque consultarItemPorId(Long id);
    List<ItemEstoqueDTO> consultarItemPorNome(String nomeItem);
    List<ItemEstoqueDTO> listarTodosItems();

}
