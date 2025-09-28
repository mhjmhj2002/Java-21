package com.example.delivery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.delivery.entity.Restaurante;

/**
 * ISP (Interface Segregation Principle):
 * Este repositório é específico para a entidade Restaurante.
 * Ele fornece apenas as operações de persistência relacionadas a restaurantes,
 * não forçando nenhuma classe a depender de métodos que não precisa.
 */
@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
    // O Spring Data JPA criará automaticamente uma query para este método.
    // Ex: SELECT * FROM restaurante WHERE nome LIKE '%nomeBusca%'
    List<Restaurante> findByNomeContainingIgnoreCase(String nome);
}
