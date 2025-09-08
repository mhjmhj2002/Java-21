package com.example.ticketsystem.entity;

public enum BatchStatus {
    PROCESSING("Processando"),
    COMPLETED("Concluído"),
    FAILED("Falhou"),
    CANCELLED("Cancelado");

    private final String descricao;

    BatchStatus(String descricao) {
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
