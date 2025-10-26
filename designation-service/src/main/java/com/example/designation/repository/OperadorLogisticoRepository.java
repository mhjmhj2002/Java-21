package com.example.designation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.designation.entity.OperadorLogistico;

public interface OperadorLogisticoRepository extends JpaRepository<OperadorLogistico, Long> {
	
	/**
     * Encontra um OperadorLogistico pelo seu nome.
     * O Spring Data JPA implementa este método automaticamente com base no nome.
     *
     * @param nome O nome do operador a ser buscado.
     * @return Um Optional contendo o operador se encontrado, ou um Optional vazio caso contrário.
     */
    Optional<OperadorLogistico> findByNome(String nome);

	
}
