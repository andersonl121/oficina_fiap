package br.com.fiap.soat15.tc_oficina.adapter.in.web;

import br.com.fiap.soat15.tc_oficina.domain.entity.Servico;
import br.com.fiap.soat15.tc_oficina.adapter.out.persistence.repository.ServicoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Sql(
        scripts = "/cleanup.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class ServicoControllerIntegrationTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ServicoRepository servicoRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("GET /api/v1/servicos - deve retornar lista vazia")
    void deveRetornarListaVazia() throws Exception {
        mockMvc.perform(get("/api/v1/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/v1/servicos - deve listar apenas serviços ativos")
    void deveListarApenasServicosAtivos() throws Exception {
        servicoRepository.save(servicoAtivo("Troca de Óleo"));
        servicoRepository.save(servicoInativo("Serviço Inativo"));

        mockMvc.perform(get("/api/v1/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Troca de Óleo")));
    }

    @Test
    @DisplayName("GET /api/v1/servicos/{id} - deve retornar serviço por ID")
    void deveBuscarServicoPorId() throws Exception {
        Servico saved = servicoRepository.save(servicoAtivo("Alinhamento"));

        mockMvc.perform(get("/api/v1/servicos/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Alinhamento")))
                .andExpect(jsonPath("$.preco", is(120.00)))
                .andExpect(jsonPath("$.tempoEstimadoMinutos", is(30)));
    }

    @Test
    @DisplayName("GET /api/v1/servicos/{id} - deve retornar 404 quando não encontrado")
    void deveRetornar404QuandoNaoEncontrado() throws Exception {
        mockMvc.perform(get("/api/v1/servicos/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/servicos - deve criar serviço com sucesso")
    void deveCriarServico() throws Exception {
        Map<String, Object> body = Map.of(
                "nome", "Troca de Óleo",
                "descricao", "Troca de óleo e filtro",
                "preco", 120.00,
                "tempoEstimadoMinutos", 30
        );

        mockMvc.perform(post("/api/v1/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Troca de Óleo")))
                .andExpect(jsonPath("$.ativo", is(true)));
    }

    @Test
    @DisplayName("POST /api/v1/servicos - deve retornar 400 com nome duplicado")
    void deveRetornarErroComNomeDuplicado() throws Exception {
        servicoRepository.save(servicoAtivo("Troca de Óleo"));

        Map<String, Object> body = Map.of(
                "nome", "Troca de Óleo",
                "preco", 150.00,
                "tempoEstimadoMinutos", 45
        );

        mockMvc.perform(post("/api/v1/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/servicos - deve retornar 400 com campos obrigatórios ausentes")
    void deveRetornarErroComCamposObrigatoriosAusentes() throws Exception {
        Map<String, Object> body = Map.of("nome", "Serviço Incompleto");

        mockMvc.perform(post("/api/v1/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/servicos/{id} - deve atualizar serviço com sucesso")
    void deveAtualizarServico() throws Exception {
        Servico saved = servicoRepository.save(servicoAtivo("Troca de Óleo"));

        Map<String, Object> body = Map.of(
                "nome", "Troca de Óleo Sintético",
                "descricao", "Óleo sintético de alta performance",
                "preco", 180.00,
                "tempoEstimadoMinutos", 45
        );

        mockMvc.perform(put("/api/v1/servicos/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Troca de Óleo Sintético")))
                .andExpect(jsonPath("$.preco", is(180.00)));
    }

    @Test
    @DisplayName("PUT /api/v1/servicos/{id} - deve retornar 404 ao atualizar inexistente")
    void deveRetornarErroAoAtualizarInexistente() throws Exception {
        Map<String, Object> body = Map.of(
                "nome", "Qualquer",
                "preco", 100.00,
                "tempoEstimadoMinutos", 30
        );

        mockMvc.perform(put("/api/v1/servicos/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/servicos/{id} - deve fazer soft delete")
    void deveDeletarServico() throws Exception {
        Servico saved = servicoRepository.save(servicoAtivo("Troca de Óleo"));

        mockMvc.perform(delete("/api/v1/servicos/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/servicos"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("DELETE /api/v1/servicos/{id} - deve retornar 404 ao deletar inexistente")
    void deveRetornarErroAoDeletarInexistente() throws Exception {
        mockMvc.perform(delete("/api/v1/servicos/{id}", 999L))
                .andExpect(status().isNotFound());
    }


    private Servico servicoAtivo(String nome) {
        return Servico.builder()
                .nome(nome)
                .descricao("Descrição do serviço")
                .preco(new BigDecimal("120.00"))
                .tempoEstimadoMinutos(30)
                .ativo(true)
                .build();
    }

    private Servico servicoInativo(String nome) {
        return Servico.builder()
                .nome(nome)
                .descricao("Serviço inativo")
                .preco(new BigDecimal("50.00"))
                .tempoEstimadoMinutos(15)
                .ativo(false)
                .build();
    }
}
