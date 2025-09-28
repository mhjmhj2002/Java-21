package com.example.delivery.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.delivery.enums.StatusPedido;

// DTO para retornar os dados de um pedido.
public record PedidoResponseDTO(
    Long id,
    Long clienteId,
    String nomeCliente,
    Long restauranteId,
    String nomeRestaurante,
    List<ItemPedidoResponseDTO> itens,
    StatusPedido status,
    BigDecimal total,
    LocalDateTime dataPedido
) {}
