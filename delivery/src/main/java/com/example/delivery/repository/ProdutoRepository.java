package com.example.delivery.repository;

import com.example.delivery.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ISP (Interface Segregation Principle):
 * Repositório focado apenas na persistência da entidade Produto.
 */
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // O Spring Data JPA cria a query para buscar todos os produtos de um restaurante específico.
    List<Produto> findByRestauranteId(Long restauranteId);
}
