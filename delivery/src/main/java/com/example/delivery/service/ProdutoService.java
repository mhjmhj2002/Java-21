package com.example.delivery.service;

import com.example.delivery.dto.ProdutoInputDTO;
import com.example.delivery.dto.ProdutoResponseDTO;
import java.util.List;

/**
 * OCP (Open/Closed Principle) & DIP (Dependency Inversion Principle):
 * Contrato para a lógica de negócio de Produto.
 */
public interface ProdutoService {
    List<ProdutoResponseDTO> listarPorRestaurante(Long restauranteId);
    ProdutoResponseDTO criar(Long restauranteId, ProdutoInputDTO produtoInputDTO);
    ProdutoResponseDTO atualizar(Long produtoId, ProdutoInputDTO produtoInputDTO);
    void deletar(Long produtoId);
}
