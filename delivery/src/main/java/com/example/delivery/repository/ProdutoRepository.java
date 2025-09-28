package com.example.delivery.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

//ISP: Esta interface contém apenas os métodos necessários para a entidade Produto.
//Um serviço que gerencia produtos não é forçado a conhecer métodos de 'Pedido' ou 'Cliente'.
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
 List<Produto> findByRestauranteId(Long restauranteId);
}

