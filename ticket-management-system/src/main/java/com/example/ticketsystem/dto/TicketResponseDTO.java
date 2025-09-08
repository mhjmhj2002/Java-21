package com.example.ticketsystem.dto;

import com.example.ticketsystem.entity.StatusTicket;
import java.time.LocalDateTime;

public class TicketResponseDTO {
    
    private Long id;
    private String titulo;
    private String descricao;
    private StatusTicket status;
    private Long clienteId;
    private String clienteNome;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    // Construtores
    public TicketResponseDTO() {}

    public TicketResponseDTO(Long id, String titulo, String descricao, StatusTicket status, 
                           Long clienteId, String clienteNome, LocalDateTime dataCriacao, 
                           LocalDateTime dataAtualizacao) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = status;
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public StatusTicket getStatus() { return status; }
    public void setStatus(StatusTicket status) { this.status = status; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}