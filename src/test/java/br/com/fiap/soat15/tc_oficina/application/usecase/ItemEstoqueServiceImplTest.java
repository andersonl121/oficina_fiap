package br.com.fiap.soat15.tc_oficina.application.usecase;

import br.com.fiap.soat15.tc_oficina.application.usecase.ItemEstoqueServiceImpl;
import br.com.fiap.soat15.tc_oficina.application.dto.ItemEstoqueDTO;
import br.com.fiap.soat15.tc_oficina.domain.entity.ItemEstoque;
import br.com.fiap.soat15.tc_oficina.adapter.out.persistence.repository.ItemEstoqueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemEstoqueServiceImplTest {

    @InjectMocks
    private ItemEstoqueServiceImpl itemEstoqueService;

    @Mock
    private ItemEstoqueRepository itemEstoqueRepository;

    private Long id;
    private ItemEstoque itemEstoque;

    @BeforeEach
    void setup() {
        id = 1L;
        itemEstoque = ItemEstoque.builder().id(1L).nome("Item Teste").descricao("Descricao").precoUnitario(BigDecimal.valueOf(100.00)).quantidadeEstoque(10).ativo(true).build();
    }

    @Test
    void deveCriarItem() throws Exception {
        when(itemEstoqueRepository.findByNomeAndAtivo(itemEstoque.getNome(), true)).thenReturn(Optional.empty());
        when(itemEstoqueRepository.save(any())).thenReturn(itemEstoque);

        ItemEstoqueDTO itemSalvo = itemEstoqueService.criarItem(toDTO(itemEstoque));

        assertThat(itemSalvo).isNotNull();
        assertThat(itemSalvo.getNome()).isEqualTo(itemEstoque.getNome());
        verify(itemEstoqueRepository).save(any(ItemEstoque.class));
    }

    @Test
    void criarITem_ComValidacaoInvalida_DeveRetornarBadRequest() throws Exception {
        when(itemEstoqueRepository.findByNomeAndAtivo(itemEstoque.getNome(), true)).thenReturn(Optional.ofNullable(itemEstoque));

        assertThatThrownBy(() -> itemEstoqueService.criarItem(toDTO(itemEstoque)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Item já cadastrado");

        verify(itemEstoqueRepository, never()).save(any());
    }


    @Test
    void deveBuscarPorId() throws Exception {
        when(itemEstoqueRepository.findByIdAndAtivo(id, true)).thenReturn(Optional.of(itemEstoque));

        ItemEstoque resultado = itemEstoqueService.consultarItemPorId(id);

        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getNome()).isEqualTo(itemEstoque.getNome());
    }

    @Test
    void obterItemPorId_ComIdInexistente_DeveRetornarBadRequest() throws Exception {
        when(itemEstoqueRepository.findByIdAndAtivo(id, true)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemEstoqueService.consultarItemPorId(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Item não encontrado");
    }

    @Test
    void deveBuscarPorNome() throws Exception {
        List<ItemEstoque> estoqueList = Collections.singletonList(itemEstoque);
        when(itemEstoqueRepository.findByAtivoAndNomeLike(itemEstoque.getNome())).thenReturn(estoqueList);

        List<ItemEstoqueDTO> resultado = itemEstoqueService.consultarItemPorNome(itemEstoque.getNome());

        assertThat(resultado.getFirst().getId()).isEqualTo(id);
        assertThat(resultado.getFirst().getNome()).isEqualTo(itemEstoque.getNome());
    }

    @Test
    void deveListarTodos() throws Exception {
        when(itemEstoqueRepository.findAllByAtivo(true)).thenReturn(List.of(itemEstoque));

        List<ItemEstoqueDTO> resultado = itemEstoqueService.listarTodosItems();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().getNome()).isEqualTo(itemEstoque.getNome());
        verify(itemEstoqueRepository).findAllByAtivo(true);
    }


    @Test
    void deveAtualizarItem() throws Exception {
        ItemEstoque itemModificado = itemEstoque;
        itemModificado.setNome("Item modificado");

        when(itemEstoqueRepository.findByIdAndAtivo(id, true)).thenReturn(Optional.ofNullable(itemEstoque));
        when(itemEstoqueService.atualizarItem(id, toDTO(itemModificado))).thenReturn(itemModificado);

        ItemEstoque itemSalvo = itemEstoqueService.atualizarItem(id, toDTO(itemModificado));

        assertThat(itemSalvo.getId()).isEqualTo(id);
        assertThat(itemSalvo.getNome()).isEqualTo(itemModificado.getNome());
        verify(itemEstoqueRepository).save(any());
    }

    @Test
    void deveAtualizarItem_ComIdInexistente_DeveRetornarBadRequest() throws Exception {
        when(itemEstoqueRepository.findByIdAndAtivo(id, true)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemEstoqueService.atualizarItem(id, toDTO(itemEstoque)))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Item não encontrado");

    }

    @Test
    void deveDeletarItem() throws Exception {
        itemEstoqueService.deletarItem(itemEstoque);

        verify(itemEstoqueRepository).save(any(ItemEstoque.class));
        assertThat(itemEstoque.getAtivo()).isFalse();
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