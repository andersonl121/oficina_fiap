package br.com.fiap.soat15.tc_oficina.domain.impl;

import br.com.fiap.soat15.tc_oficina.domain.model.ItemEstoqueDTO;
import br.com.fiap.soat15.tc_oficina.domain.service.ItemEstoqueService;
import br.com.fiap.soat15.tc_oficina.domain.validator.StringUtils;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.ItemEstoque;
import br.com.fiap.soat15.tc_oficina.infrastructure.exception.BusinessException;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.ItemEstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItemEstoqueServiceImpl implements ItemEstoqueService {

    @Autowired
    private ItemEstoqueRepository itemEstoqueRepository;


    @Override
    public ItemEstoqueDTO adicionarItem(ItemEstoqueDTO dto) {
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
        if (itemEstoque.isEmpty()) {
            throw new BusinessException("Não foram encontrados itens com o nome: " + nomeItem);
        }
        return itemEstoque.stream().map(ItemEstoque::convertToDTO).toList();
    }

    @Override
    public List<ItemEstoqueDTO> listarTodosItems() {
        List<ItemEstoque> itemEstoque = itemEstoqueRepository.findAllByAtivo(true);
        return itemEstoque.stream().map(ItemEstoque::convertToDTO).toList();
    }

    @Override
    public void deletarItem(Long id) {
        ItemEstoque itemEstoque = itemEstoqueRepository.findByIdAndAtivo(id, true)
                .orElseThrow(() -> new NoSuchElementException("Item não encontrado"));
        itemEstoque.setAtivo(false);
        itemEstoqueRepository.save(itemEstoque);
    }
}
