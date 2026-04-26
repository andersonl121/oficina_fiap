package br.com.fiap.soat15.tc_oficina.domain.model;

import br.com.fiap.soat15.tc_oficina.infrastructure.entity.ItemEstoque;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.TipoItem;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemEstoqueDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Size(max = 100, message = "Descrição deve conter no máximo 300 caracteres")
    private String descricao;

    @Positive(message = "Quantidade deve ser positiva")
    private int quantidadeEstoque;

    @Positive(message = "Preço unitário deve ser positivo")
    private BigDecimal precoUnitario;

    @Enumerated(EnumType.STRING)
    private TipoItem tipo;

    private Boolean ativo;


    public ItemEstoqueDTO(ItemEstoque itemEstoque) {
        this.id = itemEstoque.getId();
        this.nome = itemEstoque.getNome();
        this.descricao = itemEstoque.getDescricao();
        this.quantidadeEstoque = itemEstoque.getQuantidadeEstoque();
        this.precoUnitario = itemEstoque.getPrecoUnitario();
        this.tipo = itemEstoque.getTipo();
        this.ativo = itemEstoque.getAtivo();
    }

}

