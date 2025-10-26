package com.example.designation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.designation.entity.Lote;
import com.example.designation.entity.StatusLote;

public interface LoteRepository extends JpaRepository<Lote, Long> {

    /**
     * MÉTODO ADICIONADO:
     * Atualiza os contadores de um lote de forma atômica, diretamente no banco de dados.
     * Isso evita problemas de concorrência e "lost updates".
     *
     * @param loteId O ID do lote a ser atualizado.
     * @param sucessos O número de itens processados com sucesso a ser somado.
     * @param erros O número de itens com erro a ser somado.
     */
    @Modifying
    @Query("UPDATE Lote l SET l.itensProcessados = l.itensProcessados + :sucessos, l.itensComErro = l.itensComErro + :erros WHERE l.id = :loteId")
    void atualizarContadores(
        @Param("loteId") Long loteId, 
        @Param("sucessos") int sucessos, 
        @Param("erros") int erros
    );
    
 // Em LoteRepository.java
    @Modifying
    @Query("UPDATE Lote l SET l.subLotesConcluidos = l.subLotesConcluidos + 1 WHERE l.id = :loteId")
    void incrementarSubLotesConcluidos(@Param("loteId") Long loteId);
    
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
