package br.com.fiap.soat15.tc_oficina.domain;

import br.com.fiap.soat15.tc_oficina.domain.validator.DocumentoValidator;
import br.com.fiap.soat15.tc_oficina.domain.validator.PlacaValidator;
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
            "45820109050",
            "12456789001",
            "92315478022",
            "33481290034",
            "05148233081",
            "ABC12345/000120",
            "12A34B56/000138",
            "Z9Y8X7W6/000105",
            "L1M2N3O4/000192",
            "A1B2C3D4/000109"
    })
    @DisplayName("Deve aceitar cpf/cnpj válidos")
    void deveAceitarCpfCnpjValidos(String cpfCnpj) {
        assertThat(validator.isValid(cpfCnpj)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "12345678900",
            "00000000000",
            "98765432199",
            "11122233344",
            "45678912355",
            "ABC12345/000100",
            "12A34B56/000199",
            "Z9Y8X7W6/000111",
            "L1M2N3O4/000122",
            "P5Q6R7S8/000133"
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
