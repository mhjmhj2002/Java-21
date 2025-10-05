package com.example.delivery.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.delivery.dto.PedidoInputDTO;
import com.example.delivery.dto.PedidoResponseDTO;
import com.example.delivery.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * SRP (Single Responsibility Principle ):
 * Responsável apenas por expor os endpoints de Pedido e delegar a lógica.
 *
 * LSP (Liskov Substitution Principle):
 * Depende da abstração PedidoService, permitindo a troca de implementações.
 */
@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "API para gerenciamento de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }
    
    @GetMapping
    @Operation(summary = "Lista todos os pedidos do sistema")
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criar(@RequestBody PedidoInputDTO pedidoInputDTO) {
        PedidoResponseDTO novoPedido = pedidoService.criar(pedidoInputDTO);
        return new ResponseEntity<>(novoPedido, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.listarPorCliente(clienteId));
    }
    


    /**
     * SRP (Single Responsibility Principle):
     * Cada método do controller tem a única responsabilidade de expor uma ação
     * de negócio específica, delegando a execução para a camada de serviço.
     */

    @PutMapping("/{pedidoId}/confirmar")
    @Operation(summary = "Confirma um pedido")
    public ResponseEntity<PedidoResponseDTO> confirmarPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pedidoService.confirmarPedido(pedidoId));
    }

    @PutMapping("/{pedidoId}/preparar")
    @Operation(summary = "Inicia o preparo de um pedido")
    public ResponseEntity<PedidoResponseDTO> iniciarPreparo(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pedidoService.iniciarPreparo(pedidoId));
    }

    @PutMapping("/{pedidoId}/despachar")
    @Operation(summary = "Despacha um pedido para entrega")
    public ResponseEntity<PedidoResponseDTO> despacharParaEntrega(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pedidoService.despacharParaEntrega(pedidoId));
    }

    @PutMapping("/{pedidoId}/entregar")
    @Operation(summary = "Marca um pedido como entregue")
    public ResponseEntity<PedidoResponseDTO> marcarComoEntregue(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pedidoService.marcarComoEntregue(pedidoId));
    }

    @PutMapping("/{pedidoId}/cancelar")
    @Operation(summary = "Cancela um pedido")
    public ResponseEntity<PedidoResponseDTO> cancelarPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(pedidoId));
    }
}
