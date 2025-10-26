package com.example.designation.entity;

public enum StatusLote {
    PENDENTE,      // O arquivo foi recebido, aguardando processamento.
    PROCESSANDO,   // O worker começou a processar os itens.
    CONCLUIDO,     // Todos os itens foram processados com sucesso.
    FALHA_PARCIAL, // Alguns itens falharam, mas o processo terminou.
    FALHA_TOTAL    // Ocorreu um erro crítico que impediu o processamento.
}
