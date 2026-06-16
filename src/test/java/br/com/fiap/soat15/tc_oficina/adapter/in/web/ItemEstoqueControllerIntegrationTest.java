package br.com.fiap.soat15.tc_oficina.adapter.in.web;

import br.com.fiap.soat15.tc_oficina.application.dto.ItemEstoqueDTO;
import br.com.fiap.soat15.tc_oficina.domain.service.ItemEstoqueService;
import br.com.fiap.soat15.tc_oficina.domain.entity.ItemEstoque;
import br.com.fiap.soat15.tc_oficina.domain.entity.TipoItem;
import br.com.fiap.soat15.tc_oficina.adapter.in.web.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemEstoqueControllerIntegrationTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ItemEstoqueService itemEstoqueService;

    @InjectMocks
    private ItemEstoqueController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()) // 👈 AQUI
                .build();
    }

    @Test
    void deveCriarItem() throws Exception {
        ItemEstoqueDTO dto = criarDTO();
        dto.setId(1L);

        when(itemEstoqueService.criarItem(any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/item-estoque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarDTO())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void criarITem_ComValidacaoInvalida_DeveRetornarBadRequest() throws Exception {
        ItemEstoqueDTO dto = ItemEstoqueDTO.builder()
                .nome("") // Nome vazio - inválido
                .descricao("a".repeat(310)) // 310 caracteres - inválido
                .quantidadeEstoque(-10) // Quantidade negativa - inválido
                .precoUnitario(BigDecimal.valueOf(-10.00)) // Preço negativo - inválido
                .tipo(TipoItem.PECA)
                .build();

        mockMvc.perform(post("/api/v1/item-estoque")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Erro de validação nos campos fornecidos"))
                .andExpect(jsonPath("$.validationErrors").exists());

        verify(itemEstoqueService, never()).criarItem(any());
    }


    @Test
    void deveBuscarPorId() throws Exception {
        ItemEstoque entity = criarItem();

        when(itemEstoqueService.consultarItemPorId(1L)).thenReturn(entity);

        mockMvc.perform(get("/api/v1/item-estoque/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void obterItemPorId_ComIdInexistente_DeveRetornarNotFound() throws Exception {
        when(itemEstoqueService.consultarItemPorId(999L)).thenThrow(new NoSuchElementException("Item não encontrado"));

        mockMvc.perform(get("/api/v1/item-estoque/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Item não encontrado"));

        verify(itemEstoqueService, times(1)).consultarItemPorId(999L);
    }

    @Test
    void deveBuscarPorNome() throws Exception {
        when(itemEstoqueService.consultarItemPorNome("teste"))
                .thenReturn(List.of(criarDTO()));

        mockMvc.perform(get("/api/v1/item-estoque/nome/teste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void obterItemPorNome_ComNomeInexistente_DeveRetornarListaVazia() throws Exception {
        String nome = "Tenis para trilha";
        when(itemEstoqueService.consultarItemPorNome(nome)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/item-estoque/nome/" + nome))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(itemEstoqueService, times(1)).consultarItemPorNome(nome);
    }

    @Test
    void deveListarTodos() throws Exception {
        when(itemEstoqueService.listarTodosItems())
                .thenReturn(List.of(criarDTO()));

        mockMvc.perform(get("/api/v1/item-estoque"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }


    @Test
    void deveAtualizarItem() throws Exception {
        ItemEstoque entity = criarItem();

        when(itemEstoqueService.atualizarItem(any(), any())).thenReturn(entity);

        mockMvc.perform(put("/api/v1/item-estoque/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarDTO())))
                .andExpect(status().isOk());
    }

    @Test
    void deveAtualizarItem_ComIdInexistente_DeveRetornarNotFound() throws Exception {
        when(itemEstoqueService.atualizarItem(any(), any()))
                .thenThrow(new NoSuchElementException("Item não encontrado"));

        mockMvc.perform(put("/api/v1/item-estoque/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarDTO())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Item não encontrado"));
    }

    @Test
    void deveAtualizarItem_ComErroDeParametros_DeveRetornarBadRequest() throws Exception {
        ItemEstoque entity = ItemEstoque.builder()
                .nome("") // Nome vazio - inválido
                .descricao("a".repeat(310)) // 310 caracteres - inválido
                .quantidadeEstoque(-10) // Quantidade negativa - inválido
                .precoUnitario(BigDecimal.valueOf(-10.00)) // Preço negativo - inválido
                .tipo(TipoItem.PECA)
                .build();

        mockMvc.perform(put("/api/v1/item-estoque/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entity)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Erro de validação nos campos fornecidos"));

        verify(itemEstoqueService, never()).consultarItemPorNome(any());
        verify(itemEstoqueService, never()).atualizarItem(any(), any());

    }


    @Test
    void deveDeletarItem() throws Exception {
        when(itemEstoqueService.consultarItemPorId(1L)).thenReturn(criarItem());
        mockMvc.perform(delete("/api/v1/item-estoque/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletarItem_ComIdInexistente_DeveRetornarNotFound() throws Exception {
        when(itemEstoqueService.consultarItemPorId(999L)).thenThrow(new NoSuchElementException("Item não encontrado"));

        mockMvc.perform(delete("/api/v1/item-estoque/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Item não encontrado"));

        verify(itemEstoqueService, times(1)).consultarItemPorId(999L);
    }


    private ItemEstoqueDTO criarDTO() {
        ItemEstoque item = criarItem();
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

    private ItemEstoque criarItem() {
        ItemEstoque item = new ItemEstoque();
        item.setId(1L);
        item.setNome("Item Teste");
        item.setDescricao("Descricao");
        item.setPrecoUnitario(BigDecimal.valueOf(100.00));
        item.setQuantidadeEstoque(10);
        item.setAtivo(true);
        return item;
    }

}