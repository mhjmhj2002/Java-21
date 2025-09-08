package com.example.ticketsystem.entity;

public class TicketData {
    private String titulo;
    private String descricao;
    private Long clienteId;

    // Construtores
    public TicketData() {}

    public TicketData(String titulo, String descricao, Long clienteId) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.clienteId = clienteId;
    }

    // Getters e Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    @Override
    public String toString() {
        return "TicketData{" +
                "titulo='" + titulo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", clienteId=" + clienteId +
                '}';
    }
}