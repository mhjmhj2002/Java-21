package com.example.delivery.dto;

import java.time.LocalDateTime;

/**
 * DTO padrão para respostas de erro da API.
 * Fornece uma estrutura consistente para que os clientes da API
 * possam tratar os erros de forma programática.
 */
public record ErrorResponseDTO(
    LocalDateTime timestamp, // Quando o erro ocorreu
    int status,              // O código de status HTTP (ex: 404)
    String error,            // A descrição do status (ex: "Not Found")
    String message,          // A mensagem de erro específica
    String path              // O caminho da URL onde o erro ocorreu
) {}
