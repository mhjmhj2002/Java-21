package com.example.delivery.dto;

import java.math.BigDecimal;

public record ItemPedidoResponseDTO(
    Long produtoId,
    String nomeProduto,
    Integer quantidade,
    BigDecimal precoUnitario
) {}
