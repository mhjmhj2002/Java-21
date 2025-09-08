package com.example.ticketsystem.entity;

public enum StatusTicket {
    ABERTO("Aberto"),
    EM_ANDAMENTO("Em Andamento"),
    PENDENTE("Pendente"),
    RESOLVIDO("Resolvido"),
    FECHADO("Fechado"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusTicket(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}