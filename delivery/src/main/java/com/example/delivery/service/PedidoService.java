package com.example.delivery.service;

import org.springframework.stereotype.Service;

//OCP: Aberto para extensão. Outras implementações de PedidoService podem ser criadas
//(ex: PedidoServiceComPagamentoExternoImpl) sem modificar o código que usa esta interface.
public interface PedidoService {
 PedidoDTO criarPedido(PedidoInputDTO pedidoInputDTO);
}

