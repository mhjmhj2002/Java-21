package com.example.designation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.designation.entity.Lote;
import com.example.designation.entity.StatusLote;

public interface LoteRepository extends JpaRepository<Lote, Long> {
    
    /**
     * Atualiza o status de um lote.
     * Usado para finalizar o lote como CONCLUIDO ou FALHA_PARCIAL.
     *
     * @param loteId O ID do lote a ser atualizado.
     * @param novoStatus O novo status a ser definido.
     */
    @Modifying
    @Query("UPDATE Lote l SET l.status = :novoStatus WHERE l.id = :loteId")
    void atualizarStatus(
        @Param("loteId") Long loteId, 
        @Param("novoStatus") StatusLote novoStatus
    );
	
}
