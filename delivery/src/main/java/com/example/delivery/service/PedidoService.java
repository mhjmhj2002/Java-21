package com.example.delivery.service;

import com.example.delivery.dto.PedidoInputDTO;
import com.example.delivery.dto.PedidoResponseDTO;
import java.util.List;

/**
 * OCP (Open/Closed Principle) & DIP (Dependency Inversion Principle):
 * Define o contrato para a lógica de negócio de Pedidos.
 * Os controllers dependerão desta abstração, não da implementação concreta.
 */
public interface PedidoService {
    PedidoResponseDTO criar(PedidoInputDTO pedidoInputDTO);
    PedidoResponseDTO buscarPorId(Long id);
    List<PedidoResponseDTO> listarPorCliente(Long clienteId);
    
    /**
     * OCP (Open/Closed Principle):
     * Adicionamos novas funcionalidades (métodos) estendendo a interface,
     * sem modificar os métodos existentes e quem depende deles.
     */
    PedidoResponseDTO confirmarPedido(Long pedidoId);
    PedidoResponseDTO iniciarPreparo(Long pedidoId);
    PedidoResponseDTO despacharParaEntrega(Long pedidoId);
    PedidoResponseDTO marcarComoEntregue(Long pedidoId);
    PedidoResponseDTO cancelarPedido(Long pedidoId);
}
