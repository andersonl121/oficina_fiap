package br.com.fiap.soat15.tc_oficina.infrastructure.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Controller de teste para validar exception handler
        mockMvc = MockMvcBuilders
                .standaloneSetup()
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testValidationError() throws Exception {
        String requestBody = "{ \"nome\": \"\", \"cpfCnpj\": \"abc\" }";

        mockMvc.perform(post("/api/v1/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}

