package com.example.delivery.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "produto_seq_gen")
    @SequenceGenerator(name = "produto_seq_gen", sequenceName = "produto_seq", allocationSize = 1, initialValue = 1000)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Column(nullable = false)
    private BigDecimal preco;

    // Muitos produtos para um restaurante.
    // FetchType.LAZY é uma boa prática para não carregar o restaurante desnecessariamente.
    // JoinColumn define a chave estrangeira na tabela 'produto'.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;
}
