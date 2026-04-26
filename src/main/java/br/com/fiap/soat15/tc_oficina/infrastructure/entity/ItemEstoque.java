package br.com.fiap.soat15.tc_oficina.infrastructure.entity;

import br.com.fiap.soat15.tc_oficina.domain.model.ItemEstoqueDTO;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_item_estoque")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 300)
    private String descricao;

    @Column(nullable = false)
    private Integer quantidadeEstoque;

    @Column(nullable = false)
    private BigDecimal precoUnitario;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoItem tipo;

    @Column(nullable = false)
    private Boolean ativo;

    public ItemEstoqueDTO convertToDTO(){
        return new ItemEstoqueDTO(this);
    }

}
