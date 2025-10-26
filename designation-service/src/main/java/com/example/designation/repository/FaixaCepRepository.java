package com.example.designation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.designation.entity.FaixaCep;

public interface FaixaCepRepository extends JpaRepository<FaixaCep, Long> {
    // Método de consulta para encontrar a faixa que contém um CEP específico
    @Query("SELECT f FROM FaixaCep f WHERE :cep >= f.cepInicial AND :cep <= f.cepFinal")
    Optional<FaixaCep> findByCep(String cep);
    
    /**
     * Verifica se existe alguma faixa de CEP que se sobrepõe com o intervalo fornecido.
     * A sobreposição ocorre se (N_inicial <= E_final) E (N_final >= E_inicial).
     *
     * @param cepInicial O CEP inicial da nova faixa.
     * @param cepFinal O CEP final da nova faixa.
     * @return true se houver sobreposição, false caso contrário.
     */
    @Query("SELECT COUNT(f) > 0 FROM FaixaCep f WHERE f.cepInicial <= :cepFinal AND f.cepFinal >= :cepInicial")
    boolean existsOverlappingRange(@Param("cepInicial") String cepInicial, @Param("cepFinal") String cepFinal);
    
    @Query("SELECT COUNT(f) > 0 FROM FaixaCep f WHERE f.id <> :id AND f.cepInicial <= :cepFinal AND f.cepFinal >= :cepInicial")
    boolean existsOverlappingRangeForUpdate(@Param("id") Long id, @Param("cepInicial") String cepInicial, @Param("cepFinal") String cepFinal);
}
