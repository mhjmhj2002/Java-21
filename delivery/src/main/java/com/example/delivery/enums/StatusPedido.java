package com.example.delivery.enums;

/**
 * Enum para representar os possíveis status de um Pedido.
 * Usar um enum garante a consistência dos dados e evita erros de digitação
 * que poderiam ocorrer se usássemos Strings.
 */
public enum StatusPedido {
    RECEBIDO,        // O pedido foi recebido pelo sistema, mas ainda não foi confirmado pelo restaurante.
    CONFIRMADO,      // O restaurante confirmou o pedido e vai começar a prepará-lo.
    EM_PREPARO,      // O pedido está sendo preparado.
    SAIU_PARA_ENTREGA, // O pedido está a caminho do cliente.
    ENTREGUE,        // O cliente recebeu o pedido.
    CANCELADO        // O pedido foi cancelado.
}
