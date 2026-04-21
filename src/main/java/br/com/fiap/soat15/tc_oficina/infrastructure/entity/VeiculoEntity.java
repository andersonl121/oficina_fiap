package br.com.fiap.soat15.tc_oficina.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "veiculos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 8)
    private String placa;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false)
    private Integer ano;

    // TODO: descomentar quando o CRUD de cliente estiver pronto
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "cliente_id", nullable = false)
    // private ClienteEntity cliente;
}
