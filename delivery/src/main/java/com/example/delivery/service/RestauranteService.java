package com.example.delivery.service;

import com.example.delivery.dto.RestauranteDTO;
import java.util.List;

/**
 * OCP (Open/Closed Principle):
 * Esta interface define o contrato para a lógica de negócio de Restaurante.
 * Ela está aberta para extensão (podemos criar novas implementações),
 * mas fechada para modificação (os controllers que a usam não precisam ser alterados).
 */
public interface RestauranteService {
    List<RestauranteDTO> listarTodos();
    RestauranteDTO buscarPorId(Long id);
    RestauranteDTO criar(RestauranteDTO restauranteDTO);
}
