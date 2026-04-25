package br.com.fiap.soat15.tc_oficina.application;

import br.com.fiap.soat15.tc_oficina.infrastructure.entity.Cliente;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.OrdemDeServico;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.Servico;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.StatusOS;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.Veiculo;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.ClienteRepository;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.OrdemDeServicoRepository;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.ServicoRepository;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.VeiculoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class OrdemDeServicoControllerIntegrationTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired private WebApplicationContext webApplicationContext;
    @Autowired private OrdemDeServicoRepository ordemRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private VeiculoRepository veiculoRepository;
    @Autowired private ServicoRepository servicoRepository;

    private Cliente clienteSalvo;
    private Veiculo veiculoSalvo;
    private Servico servicoSalvo;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        ordemRepository.deleteAll();
        veiculoRepository.deleteAll();
        servicoRepository.deleteAll();
        clienteRepository.deleteAll();

        clienteSalvo = clienteRepository.save(Cliente.builder()
                .nome("João Silva").cpfCnpj("11144477735").ativo(true).build());

        veiculoSalvo = veiculoRepository.save(Veiculo.builder()
                .placa("ABC1D23").marca("Toyota").modelo("Corolla").ano(2020)
                .cliente(clienteSalvo).build());

        servicoSalvo = servicoRepository.save(Servico.builder()
                .nome("Troca de Óleo").descricao("Troca de óleo e filtro")
                .preco(new BigDecimal("120.00")).tempoEstimadoMinutos(30).ativo(true).build());
    }

    @Test
    @DisplayName("POST /api/v1/ordens - deve criar OS com sucesso")
    void deveCriarOrdemComSucesso() throws Exception {
        Map<String, Object> body = Map.of(
                "clienteId", clienteSalvo.getId(),
                "veiculoId", veiculoSalvo.getId(),
                "descricaoProblema", "Barulho no motor"
        );

        mockMvc.perform(post("/api/v1/ordens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.numero", startsWith("OS-")))
                .andExpect(jsonPath("$.status", is("ABERTA")))
                .andExpect(jsonPath("$.clienteId", is(clienteSalvo.getId().intValue())))
                .andExpect(jsonPath("$.valorTotal", is(0)));
    }

    @Test
    @DisplayName("POST /api/v1/ordens - deve retornar 400 com campos obrigatórios ausentes")
    void deveRetornar400ComCamposAusentes() throws Exception {
        Map<String, Object> body = Map.of("descricaoProblema", "Barulho");

        mockMvc.perform(post("/api/v1/ordens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/ordens - deve retornar 404 quando veículo não pertence ao cliente")
    void deveRetornarErroVeiculoNaoPertenceAoCliente() throws Exception {
        Cliente outroCliente = clienteRepository.save(Cliente.builder()
                .nome("Outro Cliente").cpfCnpj("52998224725").ativo(true).build());

        Map<String, Object> body = Map.of(
                "clienteId", outroCliente.getId(),
                "veiculoId", veiculoSalvo.getId(),
                "descricaoProblema", "Problema"
        );

        mockMvc.perform(post("/api/v1/ordens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/ordens - deve listar todas as ordens")
    void deveListarOrdens() throws Exception {
        criarOrdemNoBanco();

        mockMvc.perform(get("/api/v1/ordens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("ABERTA")));
    }

    @Test
    @DisplayName("GET /api/v1/ordens/{id} - deve buscar OS por ID")
    void deveBuscarOrdemPorId() throws Exception {
        OrdemDeServico ordem = criarOrdemNoBanco();

        mockMvc.perform(get("/api/v1/ordens/{id}", ordem.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ordem.getId().intValue())))
                .andExpect(jsonPath("$.descricaoProblema", is("Barulho no motor")));
    }

    @Test
    @DisplayName("GET /api/v1/ordens/{id} - deve retornar 404 quando não encontrada")
    void deveRetornar404QuandoOsNaoEncontrada() throws Exception {
        mockMvc.perform(get("/api/v1/ordens/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/ordens/cliente/{clienteId} - deve listar OS por cliente")
    void deveListarOrdensPorCliente() throws Exception {
        criarOrdemNoBanco();

        mockMvc.perform(get("/api/v1/ordens/cliente/{clienteId}", clienteSalvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("PATCH /api/v1/ordens/{id}/status - deve avançar status com sucesso")
    void deveAvancarStatus() throws Exception {
        OrdemDeServico ordem = criarOrdemNoBanco();

        Map<String, Object> body = Map.of("novoStatus", "EM_DIAGNOSTICO");

        mockMvc.perform(patch("/api/v1/ordens/{id}/status", ordem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("EM_DIAGNOSTICO")));
    }

    @Test
    @DisplayName("PATCH /api/v1/ordens/{id}/status - deve retornar 400 para transição inválida")
    void deveRetornar400ParaTransicaoInvalida() throws Exception {
        OrdemDeServico ordem = criarOrdemNoBanco();

        Map<String, Object> body = Map.of("novoStatus", "CONCLUIDA");

        mockMvc.perform(patch("/api/v1/ordens/{id}/status", ordem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/ordens/{id}/itens - deve adicionar um item à OS")
    void deveAdicionarItemNaOs() throws Exception {
        OrdemDeServico ordem = criarOrdemNoBanco();

        Map<String, Object> body = Map.of(
                "itens", List.of(Map.of("servicoId", servicoSalvo.getId(), "quantidade", 2))
        );

        mockMvc.perform(post("/api/v1/ordens/{id}/itens", ordem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itens", hasSize(1)))
                .andExpect(jsonPath("$.itens[0].servicoNome", is("Troca de Óleo")))
                .andExpect(jsonPath("$.valorTotal", is(240.0)));
    }

    @Test
    @DisplayName("POST /api/v1/ordens/{id}/itens - deve adicionar múltiplos itens em uma única chamada")
    void deveAdicionarMultiplosItens() throws Exception {
        Servico servico2 = servicoRepository.save(Servico.builder()
                .nome("Alinhamento").descricao("Alinhamento e balanceamento")
                .preco(new BigDecimal("80.00")).tempoEstimadoMinutos(60).ativo(true).build());

        OrdemDeServico ordem = criarOrdemNoBanco();

        Map<String, Object> body = Map.of(
                "itens", List.of(
                        Map.of("servicoId", servicoSalvo.getId(), "quantidade", 1),
                        Map.of("servicoId", servico2.getId(), "quantidade", 2)
                )
        );

        mockMvc.perform(post("/api/v1/ordens/{id}/itens", ordem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itens", hasSize(2)))
                .andExpect(jsonPath("$.valorTotal", is(280.0))); // 120 + (80*2)
    }

    @Test
    @DisplayName("DELETE /api/v1/ordens/{id}/itens/{itemId} - deve remover item da OS")
    void deveRemoverItemDaOs() throws Exception {
        OrdemDeServico ordem = criarOrdemNoBanco();

        Map<String, Object> addBody = Map.of(
                "itens", List.of(Map.of("servicoId", servicoSalvo.getId(), "quantidade", 1))
        );
        MvcResult addResult = mockMvc.perform(post("/api/v1/ordens/{id}/itens", ordem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addBody)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = addResult.getResponse().getContentAsString();
        Long itemId = objectMapper.readTree(responseBody).get("itens").get(0).get("id").asLong();

        mockMvc.perform(delete("/api/v1/ordens/{id}/itens/{itemId}", ordem.getId(), itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens", hasSize(0)))
                .andExpect(jsonPath("$.valorTotal", is(0)));
    }

    @Test
    @DisplayName("POST /api/v1/ordens/{id}/itens - deve retornar 400 ao adicionar item em OS aprovada")
    void deveRetornar400AoAdicionarItemEmOsAprovada() throws Exception {
        OrdemDeServico ordem = criarOrdemNoBanco();

        avancarStatusBanco(ordem.getId(), "EM_DIAGNOSTICO");
        avancarStatusBanco(ordem.getId(), "AGUARDANDO_APROVACAO");
        avancarStatusBanco(ordem.getId(), "APROVADA");

        Map<String, Object> body = Map.of(
                "itens", List.of(Map.of("servicoId", servicoSalvo.getId(), "quantidade", 1))
        );

        mockMvc.perform(post("/api/v1/ordens/{id}/itens", ordem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    // --- helpers ---

    private OrdemDeServico criarOrdemNoBanco() {
        return ordemRepository.save(OrdemDeServico.builder()
                .numero("OS-2025-" + String.format("%04d", ordemRepository.count() + 1))
                .veiculo(veiculoSalvo)
                .status(StatusOS.ABERTA)
                .dataAbertura(LocalDateTime.now())
                .descricaoProblema("Barulho no motor")
                .valorTotal(BigDecimal.ZERO)
                .build());
    }

    private void avancarStatusBanco(Long ordemId, String novoStatus) throws Exception {
        Map<String, Object> body = Map.of("novoStatus", novoStatus);
        mockMvc.perform(patch("/api/v1/ordens/{id}/status", ordemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }
}
