package br.com.fiap.soat15.tc_oficina.application;

import br.com.fiap.soat15.tc_oficina.domain.model.ItemEstoqueDTO;
import br.com.fiap.soat15.tc_oficina.domain.service.ItemEstoqueService;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.ItemEstoque;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
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
    void deveBuscarPorId() throws Exception {
        ItemEstoque entity = criarItem();

        when(itemEstoqueService.consultarItemPorId(1L)).thenReturn(entity);

        mockMvc.perform(get("/api/v1/item-estoque/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
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
    void deveDeletarItem() throws Exception {
        mockMvc.perform(delete("/api/v1/item-estoque/1"))
                .andExpect(status().isNoContent());
    }


    private ItemEstoqueDTO criarDTO() {
        ItemEstoqueDTO dto = new ItemEstoqueDTO();
        dto.setId(1L);
        dto.setNome("Item Teste");
        dto.setDescricao("Descricao");
        dto.setPrecoUnitario(BigDecimal.valueOf(100.00));
        dto.setQuantidadeEstoque(10);
        dto.setAtivo(true);
        return dto;
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