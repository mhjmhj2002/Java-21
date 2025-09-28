package com.example.delivery.dto;

import java.math.BigDecimal;

// DTO para retornar os dados de um produto.
public record ProdutoResponseDTO(
    Long id,
    String nome,
    String descricao,
    BigDecimal preco,
    Long restauranteId
) {}
