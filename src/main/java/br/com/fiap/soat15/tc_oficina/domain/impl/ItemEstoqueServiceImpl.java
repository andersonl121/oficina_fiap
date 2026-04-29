package br.com.fiap.soat15.tc_oficina.domain.impl;

import br.com.fiap.soat15.tc_oficina.domain.model.ItemEstoqueDTO;
import br.com.fiap.soat15.tc_oficina.domain.service.ItemEstoqueService;
import br.com.fiap.soat15.tc_oficina.domain.validator.StringUtils;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.ItemEstoque;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.ItemEstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItemEstoqueServiceImpl implements ItemEstoqueService {

    @Autowired
    private ItemEstoqueRepository itemEstoqueRepository;


    @Override
    public ItemEstoqueDTO criarItem(ItemEstoqueDTO dto) {
        if (itemEstoqueRepository.findByNomeAndAtivo(dto.getNome(), true).isPresent()) {
            throw new IllegalArgumentException("Item já cadastrado");
        }

        ItemEstoque itemEstoque = ItemEstoque.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .quantidadeEstoque(dto.getQuantidadeEstoque())
                .precoUnitario(dto.getPrecoUnitario())
                .tipo(dto.getTipo())
                .ativo(true)
                .build();

        return itemEstoqueRepository.save(itemEstoque).convertToDTO();
    }

    @Override
    public ItemEstoque atualizarItem(Long id, ItemEstoqueDTO itemAtualizado) {
        ItemEstoque itemEstoque = itemEstoqueRepository.findByIdAndAtivo(id, true)
                .orElseThrow(() -> new NoSuchElementException("Item não encontrado"));

        itemEstoque.setNome(StringUtils.removerAcentos(itemAtualizado.getNome()));
        itemEstoque.setDescricao(itemAtualizado.getDescricao());
        itemEstoque.setQuantidadeEstoque(itemAtualizado.getQuantidadeEstoque());
        itemEstoque.setPrecoUnitario(itemAtualizado.getPrecoUnitario());
        itemEstoque.setTipo(itemAtualizado.getTipo());

        return itemEstoqueRepository.save(itemEstoque);
    }

    @Override
    public ItemEstoque consultarItemPorId(Long id) {
        return itemEstoqueRepository.findByIdAndAtivo(id, true)
                .orElseThrow(() -> new NoSuchElementException("Item não encontrado"));
    }

    @Override
    public List<ItemEstoqueDTO> consultarItemPorNome(String nomeItem) {
        List<ItemEstoque> itemEstoque = itemEstoqueRepository.findByAtivoAndNomeLike(StringUtils.removerAcentos(nomeItem));
        if (itemEstoque == null || itemEstoque.isEmpty()) {
            return new ArrayList<>();
        }
        return itemEstoque.stream().map(ItemEstoque::convertToDTO).toList();
    }

    @Override
    public List<ItemEstoqueDTO> listarTodosItems() {
        List<ItemEstoque> itemEstoque = itemEstoqueRepository.findAllByAtivo(true);
        return itemEstoque.stream().map(ItemEstoque::convertToDTO).toList();
    }

    @Override
    public ItemEstoqueDTO aumentarEstoque(Long id, int quantidade) {
        ItemEstoque itemEstoque = this.consultarItemPorId(id);
        itemEstoque.aumentarEstoque(quantidade);

        return itemEstoqueRepository.save(itemEstoque).convertToDTO();
    }

    @Override
    public ItemEstoqueDTO diminuirEstoque(Long id, int quantidade) {
        ItemEstoque itemEstoque = this.consultarItemPorId(id);
        itemEstoque.reduzirEstoque(quantidade);

        return itemEstoqueRepository.save(itemEstoque).convertToDTO();
    }

    @Override
    public BigDecimal calculartotal(Long id, int quantidade) {
        ItemEstoque itemEstoque = this.consultarItemPorId(id);

        return itemEstoque.calcularCustoTotal(quantidade);
    }

    @Override
    public void deletarItem(ItemEstoque itemEstoque) {
        itemEstoque.setAtivo(false);
        itemEstoqueRepository.save(itemEstoque);
    }
}

