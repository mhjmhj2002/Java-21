package com.example.delivery.entity;

import com.example.delivery.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pedido_seq_gen")
    @SequenceGenerator(name = "pedido_seq_gen", sequenceName = "pedido_seq", allocationSize = 1, initialValue = 1000)
    private Long id;

    // Muitos pedidos para um cliente.
    @ManyToOne(fetch = FetchType.LAZY) // LAZY: Carrega o cliente apenas quando for acessado.
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Muitos pedidos para um restaurante.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    // Um pedido tem muitos itens.
    // CascadeType.ALL: Se um pedido for salvo ou deletado, seus itens também serão.
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @Enumerated(EnumType.STRING) // Grava o nome do enum ("RECEBIDO") no banco, não o número.
    @Column(nullable = false)
    private StatusPedido status;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(nullable = false)
    private LocalDateTime dataPedido;

    // Método utilitário para adicionar itens ao pedido de forma segura,
    // garantindo a consistência do relacionamento bidirecional.
    public void adicionarItem(ItemPedido item) {
        itens.add(item);
        item.setPedido(this);
    }
}
