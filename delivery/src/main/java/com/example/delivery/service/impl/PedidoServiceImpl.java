package com.example.delivery.service.impl;

import com.example.delivery.dto.*;
import com.example.delivery.entity.*;
import com.example.delivery.enums.StatusPedido;
import com.example.delivery.mapper.PedidoMapper;
import com.example.delivery.repository.*;
import com.example.delivery.service.PedidoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * SRP (Single Responsibility Principle):
 * A responsabilidade desta classe é a lógica de negócio para Pedidos.
 * Ela orquestra validações, cálculos e persistência, mantendo o controller "limpo".
 */
@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoMapper pedidoMapper;

    // DIP: Injetando abstrações (repositórios e mapper) via construtor.
    public PedidoServiceImpl(PedidoRepository pedidoRepository, ClienteRepository clienteRepository, RestauranteRepository restauranteRepository, ProdutoRepository produtoRepository, PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoMapper = pedidoMapper;
    }
    
    @Override
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll().stream()
                .map(pedidoMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional // Garante que todas as operações de banco de dados ocorram em uma única transação.
    public PedidoResponseDTO criar(PedidoInputDTO pedidoInputDTO) {
        // 1. Buscar entidades relacionadas
        Cliente cliente = clienteRepository.findById(pedidoInputDTO.clienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        Restaurante restaurante = restauranteRepository.findById(pedidoInputDTO.restauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.RECEBIDO);

        BigDecimal totalPedido = BigDecimal.ZERO;

        // 2. Processar os itens do pedido
        for (ItemPedidoInputDTO itemDTO : pedidoInputDTO.itens()) {
            Produto produto = produtoRepository.findById(itemDTO.produtoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + itemDTO.produtoId()));

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemDTO.quantidade());
            itemPedido.setPrecoUnitario(produto.getPreco()); // Grava o preço do momento da compra

            pedido.adicionarItem(itemPedido); // Adiciona o item ao pedido

            // 3. Calcular o total
            totalPedido = totalPedido.add(produto.getPreco().multiply(BigDecimal.valueOf(itemDTO.quantidade())));
        }

        pedido.setTotal(totalPedido);

        // 4. Salvar o pedido e seus itens (graças ao CascadeType.ALL)
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // 5. Retornar o DTO de resposta
        return pedidoMapper.toResponseDTO(pedidoSalvo);
    }

    @Override
    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));
        return pedidoMapper.toResponseDTO(pedido);
    }

    @Override
    public List<PedidoResponseDTO> listarPorCliente(Long clienteId) {
        List<Pedido> pedidos = pedidoRepository.findByClienteId(clienteId);
        return pedidoMapper.toResponseDTOList(pedidos);
    }
    
    /**
     * Método privado auxiliar para encontrar um pedido ou lançar uma exceção.
     * Evita a repetição de código nos métodos de mudança de status.
     */
    private Pedido findPedidoById(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com id: " + pedidoId));
    }

    @Override
    @Transactional
    public PedidoResponseDTO confirmarPedido(Long pedidoId) {
        Pedido pedido = findPedidoById(pedidoId);
        // Regra de negócio: Só pode confirmar um pedido que foi 'RECEBIDO'.
        if (pedido.getStatus() != StatusPedido.RECEBIDO) {
            throw new IllegalStateException("O pedido não pode ser confirmado, pois seu status atual é: " + pedido.getStatus());
        }
        pedido.setStatus(StatusPedido.CONFIRMADO);
        return pedidoMapper.toResponseDTO(pedidoRepository.save(pedido));
    }

    @Override
    @Transactional
    public PedidoResponseDTO iniciarPreparo(Long pedidoId) {
        Pedido pedido = findPedidoById(pedidoId);
        // Regra de negócio: Só pode iniciar o preparo de um pedido 'CONFIRMADO'.
        if (pedido.getStatus() != StatusPedido.CONFIRMADO) {
            throw new IllegalStateException("O pedido não pode iniciar o preparo, pois seu status atual é: " + pedido.getStatus());
        }
        pedido.setStatus(StatusPedido.EM_PREPARO);
        return pedidoMapper.toResponseDTO(pedidoRepository.save(pedido));
    }

    @Override
    @Transactional
    public PedidoResponseDTO despacharParaEntrega(Long pedidoId) {
        Pedido pedido = findPedidoById(pedidoId);
        if (pedido.getStatus() != StatusPedido.EM_PREPARO) {
            throw new IllegalStateException("O pedido não pode sair para entrega, pois seu status atual é: " + pedido.getStatus());
        }
        pedido.setStatus(StatusPedido.SAIU_PARA_ENTREGA);
        return pedidoMapper.toResponseDTO(pedidoRepository.save(pedido));
    }

    @Override
    @Transactional
    public PedidoResponseDTO marcarComoEntregue(Long pedidoId) {
        Pedido pedido = findPedidoById(pedidoId);
        if (pedido.getStatus() != StatusPedido.SAIU_PARA_ENTREGA) {
            throw new IllegalStateException("O pedido não pode ser marcado como entregue, pois seu status atual é: " + pedido.getStatus());
        }
        pedido.setStatus(StatusPedido.ENTREGUE);
        return pedidoMapper.toResponseDTO(pedidoRepository.save(pedido));
    }

    @Override
    @Transactional
    public PedidoResponseDTO cancelarPedido(Long pedidoId) {
        Pedido pedido = findPedidoById(pedidoId);
        // Regra de negócio: Não pode cancelar um pedido que já foi entregue.
        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new IllegalStateException("Não é possível cancelar um pedido que já foi entregue.");
        }
        pedido.setStatus(StatusPedido.CANCELADO);
        return pedidoMapper.toResponseDTO(pedidoRepository.save(pedido));
    }
    
}
