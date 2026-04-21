package br.com.fiap.soat15.tc_oficina.application;

import br.com.fiap.soat15.tc_oficina.infrastructure.entity.VeiculoEntity;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.VeiculoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;



import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class VeiculoControllerIntegrationTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        veiculoRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /veiculos - deve retornar lista vazia")
    void deveRetornarListaVazia() throws Exception {
        mockMvc.perform(get("/veiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /veiculos - deve listar veículos cadastrados")
    void deveListarVeiculos() throws Exception {
        veiculoRepository.save(veiculoEntity("ABC1D23"));
        veiculoRepository.save(veiculoEntity("XYZ9W87"));

        mockMvc.perform(get("/veiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /veiculos/{id} - deve retornar veículo por ID")
    void deveBuscarVeiculoPorId() throws Exception {
        VeiculoEntity saved = veiculoRepository.save(veiculoEntity("ABC1D23"));

        mockMvc.perform(get("/veiculos/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa", is("ABC1D23")))
                .andExpect(jsonPath("$.marca", is("Toyota")))
                .andExpect(jsonPath("$.modelo", is("Corolla")));
    }

    @Test
    @DisplayName("GET /veiculos/{id} - deve retornar 404 quando não encontrado")
    void deveRetornarErroQuandoVeiculoNaoEncontrado() throws Exception {
        mockMvc.perform(get("/veiculos/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /veiculos - deve criar veículo com sucesso")
    void deveCriarVeiculo() throws Exception {
        Map<String, Object> body = Map.of(
                "placa", "ABC1D23",
                "marca", "Toyota",
                "modelo", "Corolla",
                "ano", 2022
        );

        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.placa", is("ABC1D23")));
    }

    @Test
    @DisplayName("POST /veiculos - deve retornar 422 com placa duplicada")
    void deveRetornarErroComPlacaDuplicada() throws Exception {
        veiculoRepository.save(veiculoEntity("ABC1D23"));

        Map<String, Object> body = Map.of(
                "placa", "ABC1D23",
                "marca", "Honda",
                "modelo", "Civic",
                "ano", 2023
        );

        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /veiculos/{id} - deve atualizar veículo com sucesso")
    void deveAtualizarVeiculo() throws Exception {
        VeiculoEntity saved = veiculoRepository.save(veiculoEntity("ABC1D23"));

        Map<String, Object> body = Map.of(
                "placa", "XYZ9W87",
                "marca", "Honda",
                "modelo", "Civic",
                "ano", 2023
        );

        mockMvc.perform(put("/veiculos/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marca", is("Honda")))
                .andExpect(jsonPath("$.placa", is("XYZ9W87")));
    }

    @Test
    @DisplayName("DELETE /veiculos/{id} - deve deletar veículo com sucesso")
    void deveDeletarVeiculo() throws Exception {
        VeiculoEntity saved = veiculoRepository.save(veiculoEntity("ABC1D23"));

        mockMvc.perform(delete("/veiculos/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /veiculos/{id} - deve retornar 404 ao deletar inexistente")
    void deveRetornarErroAoDeletarInexistente() throws Exception {
        mockMvc.perform(delete("/veiculos/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    private VeiculoEntity veiculoEntity(String placa) {
        return VeiculoEntity.builder()
                .placa(placa)
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2022)
                .build();
    }
}
