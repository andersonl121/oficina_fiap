package br.com.fiap.soat15.tc_oficina.domain;

import br.com.fiap.soat15.tc_oficina.domain.validator.DocumentoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentoValidatorTest {

    private DocumentoValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DocumentoValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            // CPFs válidos (dígitos verificadores corretos)
            "11144477735",
            "52998224725",
            "12345678909",
            "98765432100",
            "54938271079",
            // CNPJs alfanuméricos válidos (14 chars, somente maiúsculas/dígitos)
            "ABC12345000120",
            "12A34B56000138",
            "Z9Y8X7W6000105",
            "L1M2N3O4000192",
            "A1B2C3D4000109"
    })
    @DisplayName("Deve aceitar cpf/cnpj válidos")
    void deveAceitarCpfCnpjValidos(String cpfCnpj) {
        assertThat(validator.isValid(cpfCnpj)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "12345678900",    // CPF com d2 errado (correto seria 12345678909)
            "00000000000",    // todos dígitos iguais
            "98765432199",    // CPF com dígitos verificadores errados
            "11122233344",    // CPF com dígitos verificadores errados
            "45678912355",    // CPF com dígitos verificadores errados
            "abc12345000100", // CNPJ alfanumérico com letras minúsculas
            "12a34b56000199", // CNPJ alfanumérico com letras minúsculas
            "ABC1234500012",  // CNPJ alfanumérico com 13 chars (tamanho inválido)
            "ABCDEFGHIJKLMNO", // CNPJ alfanumérico com 15 chars (tamanho inválido)
            "A1B2C3D"         // CNPJ alfanumérico com 7 chars (tamanho inválido)
    })
    @DisplayName("Deve rejeitar cpf/cnpj inválidos")
    void deveRejeitarCpfCnpjInvalidos(String cpfCnpj) {
        assertThat(validator.isValid(cpfCnpj)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"  ", "\t"})
    @DisplayName("Deve rejeitar cpf/cnpj em branco")
    void deveRejeitarCpfCnpjEmBranco(String cpfCnpj) {
        assertThat(validator.isValid(cpfCnpj)).isFalse();
    }
}
