package com.example.delivery.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.delivery.dto.PedidoInputDTO;
import com.example.delivery.dto.PedidoResponseDTO;
import com.example.delivery.enums.StatusPedido;

/**
 * OCP (Open/Closed Principle) & DIP (Dependency Inversion Principle):
 * Define o contrato para a lógica de negócio de Pedidos.
 * Os controllers dependerão desta abstração, não da implementação concreta.
 */
public interface PedidoService {
	List<PedidoResponseDTO> listarTodos();
    PedidoResponseDTO criar(PedidoInputDTO pedidoInputDTO);
    PedidoResponseDTO buscarPorId(Long id);
    List<PedidoResponseDTO> listarPorCliente(Long clienteId);
    Page<PedidoResponseDTO> buscar(Long clienteId, Long restauranteId, StatusPedido status, Pageable pageable); // NOVO MÉTODO
    
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
