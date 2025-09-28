package com.example.delivery.controller;

import com.example.delivery.dto.ProdutoInputDTO;
import com.example.delivery.dto.ProdutoResponseDTO;
import com.example.delivery.service.ProdutoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SRP (Single Responsibility Principle ):
 * Gerencia os endpoints REST para Produto, delegando a lógica para o service.
 */
@RestController
@RequestMapping("/api/restaurantes/{restauranteId}/produtos")
@Tag(name = "Produtos", description = "API para gerenciamento de produtos de um restaurante")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarPorRestaurante(@PathVariable Long restauranteId) {
        return ResponseEntity.ok(produtoService.listarPorRestaurante(restauranteId));
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(
            @PathVariable Long restauranteId,
            @Valid @RequestBody ProdutoInputDTO produtoInputDTO) { // @Valid ativa as validações do DTO
        ProdutoResponseDTO novoProduto = produtoService.criar(restauranteId, produtoInputDTO);
        return new ResponseEntity<>(novoProduto, HttpStatus.CREATED);
    }

    // Para atualizar, usamos um endpoint focado no produto, mas a lógica de serviço ainda pode
    // verificar a consistência com o restaurante se necessário.
    @PutMapping("/{produtoId}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long restauranteId, // Usado para contexto e validação de rota
            @PathVariable Long produtoId,
            @Valid @RequestBody ProdutoInputDTO produtoInputDTO) {
        // Poderíamos adicionar uma verificação para garantir que produtoId pertence a restauranteId
        return ResponseEntity.ok(produtoService.atualizar(produtoId, produtoInputDTO));
    }

    @DeleteMapping("/{produtoId}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long restauranteId, // Usado para contexto
            @PathVariable Long produtoId) {
        produtoService.deletar(produtoId);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content, sucesso sem corpo
    }
}
