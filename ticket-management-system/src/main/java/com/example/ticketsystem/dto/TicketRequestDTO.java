package com.example.ticketsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TicketRequestDTO {
    
    @NotBlank(message = "Título é obrigatório")
    private String titulo;
    
    private String descricao;
    
    @NotBlank(message = "Status é obrigatório")
    private String status;
    
    @NotNull(message = "Cliente é obrigatório")
    private Long clienteId;

    // Construtores
    public TicketRequestDTO() {}

    public TicketRequestDTO(String titulo, String descricao, String status, Long clienteId) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = status;
        this.clienteId = clienteId;
    }

    // Getters e Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
}