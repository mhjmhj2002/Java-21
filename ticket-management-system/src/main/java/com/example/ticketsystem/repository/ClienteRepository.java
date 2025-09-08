package com.example.ticketsystem.repository;

import com.example.ticketsystem.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);
    
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
    
    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Cliente> searchByNome(@Param("nome") String nome);
    
    @Query("SELECT c FROM Cliente c WHERE c.email = :email AND c.id != :id")
    Optional<Cliente> findByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);
    
    @Query("SELECT COUNT(c) FROM Cliente c")
    long countAll();
    
    boolean existsByEmail(String email);
}