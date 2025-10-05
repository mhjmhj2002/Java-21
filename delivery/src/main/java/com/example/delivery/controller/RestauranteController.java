package com.example.delivery.controller;

import com.example.delivery.dto.RestauranteDTO;
import com.example.delivery.service.RestauranteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SRP (Single Responsibility Principle ):
 * A única responsabilidade desta classe é gerenciar os endpoints REST para Restaurante.
 * Ela recebe requisições HTTP, delega a lógica para o service e retorna a resposta.
 *
 * LSP (Liskov Substitution Principle):
 * Esta classe depende da interface RestauranteService. Qualquer implementação
 * que siga o contrato da interface pode ser injetada aqui sem quebrar o código.
 */
@RestController
@RequestMapping("/api/restaurantes")
@Tag(name = "Restaurantes", description = "API para gerenciamento de restaurantes")
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    @GetMapping
    public ResponseEntity<List<RestauranteDTO>> listarTodos() {
        return ResponseEntity.ok(restauranteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestauranteDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(restauranteService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<RestauranteDTO> criar(@RequestBody RestauranteDTO restauranteDTO) {
        RestauranteDTO novoRestaurante = restauranteService.criar(restauranteDTO);
        return new ResponseEntity<>(novoRestaurante, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RestauranteDTO> atualizar(@PathVariable Long id, @RequestBody RestauranteDTO restauranteDTO) {
        RestauranteDTO restauranteAtualizado = restauranteService.atualizar(id, restauranteDTO);
        return ResponseEntity.ok(restauranteAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        restauranteService.deletar(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}
