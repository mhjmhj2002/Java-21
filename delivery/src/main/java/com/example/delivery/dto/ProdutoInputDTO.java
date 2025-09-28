package com.example.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

// DTO para receber dados de criação/atualização de um produto.
// Adicionamos validações para garantir a qualidade dos dados de entrada.
public record ProdutoInputDTO(
    @NotBlank(message = "O nome não pode ser vazio.")
    String nome,

    String descricao,

    @NotNull(message = "O preço não pode ser nulo.")
    @Positive(message = "O preço deve ser um valor positivo.")
    BigDecimal preco
) {}
