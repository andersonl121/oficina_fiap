package br.com.fiap.soat15.tc_oficina.domain.service;

import br.com.fiap.soat15.tc_oficina.domain.model.ItemEstoqueDTO;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.ItemEstoque;

import java.math.BigDecimal;
import java.util.List;

public interface ItemEstoqueService {

    ItemEstoqueDTO criarItem(ItemEstoqueDTO itemEstoque);

    ItemEstoque atualizarItem(Long id, ItemEstoqueDTO itemAtualizado);

    void deletarItem(ItemEstoque itemEstoque);

    ItemEstoque consultarItemPorId(Long id);

    List<ItemEstoqueDTO> consultarItemPorNome(String nomeItem);

    List<ItemEstoqueDTO> listarTodosItems();

    ItemEstoqueDTO aumentarEstoque(Long id, int quantidade);

    ItemEstoqueDTO diminuirEstoque(Long id, int quantidade);

    BigDecimal calculartotal(Long id, int quantidade);

}
