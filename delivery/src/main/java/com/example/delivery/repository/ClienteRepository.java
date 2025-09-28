package com.example.delivery.repository;

import com.example.delivery.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ISP (Interface Segregation Principle):
 * Repositório focado apenas na persistência da entidade Cliente.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Método para buscar um cliente pelo e-mail, útil para evitar duplicatas.
    Optional<Cliente> findByEmail(String email);
}
