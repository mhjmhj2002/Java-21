package com.example.delivery.dto;

// Usar um 'record' é uma forma moderna e concisa de criar DTOs imutáveis.
// Ele automaticamente gera construtor, getters, equals(), hashCode() e toString().
public record RestauranteDTO(
    Long id,
    String nome,
    String endereco
) {}
