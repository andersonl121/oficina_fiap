package br.com.fiap.soat15.tc_oficina.application;

import br.com.fiap.soat15.tc_oficina.domain.model.ItemEstoqueDTO;
import br.com.fiap.soat15.tc_oficina.domain.service.ItemEstoqueService;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.ItemEstoque;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.TipoItem;
import br.com.fiap.soat15.tc_oficina.infrastructure.exception.GlobalExceptionHandler;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemEstoqueControllerTest {

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

        when(itemEstoqueService.adicionarItem(any())).thenReturn(dto);

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

        verify(itemEstoqueService, never()).adicionarItem(any());
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
    void obterItemPorId_ComIdInexistente_DeveRetornarBadRequest() throws Exception {
        when(itemEstoqueService.consultarItemPorId(999L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/item-estoque/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("item com ID " + 999 + " não encontrado"));

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
    void obterItemPorNome_ComNomeInexistente_DeveRetornarBadRequest() throws Exception {
        String nome = "Tenis para trilha";
        when(itemEstoqueService.consultarItemPorNome(nome)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/item-estoque/nome/" + nome))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Não foram encontrados itens com o nome: " + nome));

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

        when(itemEstoqueService.consultarItemPorId(1L)).thenReturn(entity);
        when(itemEstoqueService.atualizarItem(any(), any())).thenReturn(entity);

        mockMvc.perform(put("/api/v1/item-estoque/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarDTO())))
                .andExpect(status().isOk());
    }

    @Test
    void deveAtualizarItem_ComIdInexistente_DeveRetornarBadRequest() throws Exception {
        ItemEstoque entity = criarItem();
        entity.setId(999L);

        when(itemEstoqueService.consultarItemPorId(999L)).thenReturn(null);

        mockMvc.perform(put("/api/v1/item-estoque/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entity)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Item com ID " + 999 + " não encontrado"));

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
        mockMvc.perform(delete("/api/v1/item-estoque/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletarItem_ComIdInexistente_DeveRetornarBadRequest() throws Exception {
        when(itemEstoqueService.consultarItemPorId(999L)).thenReturn(null);

        mockMvc.perform(delete("/api/v1/item-estoque/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Item não encontrado"));

        verify(itemEstoqueService, times(1)).consultarItemPorId(999L);
    }


    private ItemEstoqueDTO criarDTO() {
        return criarItem().convertToDTO();
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