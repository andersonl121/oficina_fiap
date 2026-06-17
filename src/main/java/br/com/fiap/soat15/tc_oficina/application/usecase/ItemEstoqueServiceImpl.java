package br.com.fiap.soat15.tc_oficina.application.usecase;

import br.com.fiap.soat15.tc_oficina.adapter.out.persistence.repository.ItemEstoqueRepository;
import br.com.fiap.soat15.tc_oficina.application.dto.ItemEstoqueDTO;
import br.com.fiap.soat15.tc_oficina.domain.entity.ItemEstoque;
import br.com.fiap.soat15.tc_oficina.domain.service.ItemEstoqueService;
import br.com.fiap.soat15.tc_oficina.domain.validator.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class ItemEstoqueServiceImpl implements ItemEstoqueService {

    private final ItemEstoqueRepository itemEstoqueRepository;

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

        return toDTO(itemEstoqueRepository.save(itemEstoque));
    }

    @Override
    public ItemEstoqueDTO atualizarItem(Long id, ItemEstoqueDTO itemAtualizado) {
        ItemEstoque itemEstoque = buscarEntidade(id);

        itemEstoque.setNome(StringUtils.removerAcentos(itemAtualizado.getNome()));
        itemEstoque.setDescricao(itemAtualizado.getDescricao());
        itemEstoque.setQuantidadeEstoque(itemAtualizado.getQuantidadeEstoque());
        itemEstoque.setPrecoUnitario(itemAtualizado.getPrecoUnitario());
        itemEstoque.setTipo(itemAtualizado.getTipo());

        return toDTO(itemEstoqueRepository.save(itemEstoque));
    }

    @Override
    public ItemEstoqueDTO consultarItemPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    @Override
    public List<ItemEstoqueDTO> consultarItemPorNome(String nomeItem) {
        List<ItemEstoque> itemEstoque = itemEstoqueRepository.findByAtivoAndNomeLike(StringUtils.removerAcentos(nomeItem));
        if (itemEstoque == null || itemEstoque.isEmpty()) {
            return new ArrayList<>();
        }
        return itemEstoque.stream().map(this::toDTO).toList();
    }

    @Override
    public List<ItemEstoqueDTO> listarTodosItems() {
        List<ItemEstoque> itemEstoque = itemEstoqueRepository.findAllByAtivo(true);
        return itemEstoque.stream().map(this::toDTO).toList();
    }

    @Override
    public ItemEstoqueDTO aumentarEstoque(Long id, int quantidade) {
        ItemEstoque itemEstoque = buscarEntidade(id);
        itemEstoque.aumentarEstoque(quantidade);
        return toDTO(itemEstoqueRepository.save(itemEstoque));
    }

    @Override
    public ItemEstoqueDTO diminuirEstoque(Long id, int quantidade) {
        ItemEstoque itemEstoque = buscarEntidade(id);
        itemEstoque.reduzirEstoque(quantidade);
        return toDTO(itemEstoqueRepository.save(itemEstoque));
    }

    @Override
    public BigDecimal calculartotal(Long id, int quantidade) {
        ItemEstoque itemEstoque = buscarEntidade(id);
        return itemEstoque.calcularCustoTotal(quantidade);
    }

    @Override
    public void deletarItem(Long id) {
        ItemEstoque itemEstoque = buscarEntidade(id);
        itemEstoque.setAtivo(false);
        itemEstoqueRepository.save(itemEstoque);
    }

    private ItemEstoque buscarEntidade(Long id) {
        return itemEstoqueRepository.findByIdAndAtivo(id, true)
                .orElseThrow(() -> new NoSuchElementException("Item não encontrado"));
    }

    private ItemEstoqueDTO toDTO(ItemEstoque item) {
        return ItemEstoqueDTO.builder()
                .id(item.getId())
                .nome(item.getNome())
                .descricao(item.getDescricao())
                .quantidadeEstoque(item.getQuantidadeEstoque())
                .precoUnitario(item.getPrecoUnitario())
                .tipo(item.getTipo())
                .ativo(item.getAtivo())
                .build();
    }
}
