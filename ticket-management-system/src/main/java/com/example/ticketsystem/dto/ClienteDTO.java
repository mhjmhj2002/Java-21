package com.example.ticketsystem.dto;

import java.time.LocalDateTime;

public class ClienteDTO {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private LocalDateTime dataCriacao;
    private int totalTickets;

    // Construtores
    public ClienteDTO() {}

    public ClienteDTO(Long id, String nome, String email, String telefone, LocalDateTime dataCriacao, int totalTickets) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.dataCriacao = dataCriacao;
        this.totalTickets = totalTickets;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public int getTotalTickets() { return totalTickets; }
    public void setTotalTickets(int totalTickets) { this.totalTickets = totalTickets; }
}