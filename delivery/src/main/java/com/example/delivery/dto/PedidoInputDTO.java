package com.example.delivery.dto;

import java.util.List;

// DTO para criar um novo pedido.
public record PedidoInputDTO(
    Long clienteId,
    Long restauranteId,
    List<ItemPedidoInputDTO> itens
) {}
