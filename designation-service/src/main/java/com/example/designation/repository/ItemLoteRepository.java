package com.example.designation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.designation.entity.ItemLote;
import com.example.designation.entity.StatusProcessamento;

@Repository
public interface ItemLoteRepository extends JpaRepository<ItemLote, Long> {

    /**
     * MÉTODO ADICIONADO:
     * Busca uma lista de itens de lote com base no ID do sub-lote (UUID em String)
     * e no status de processamento.
     * Este é o método que o LoteProcessingService usa para encontrar o trabalho a ser feito.
     */
    List<ItemLote> findByLoteIdAndStatus(String loteId, StatusProcessamento status);

    /**
     * Busca uma página de itens de lote com base no ID do lote "pai".
     * Este é o método que o LoteController usa para exibir os detalhes de um upload
     * na interface do usuário.
     * O nome do método deve seguir a convenção do Spring Data: findBy<NomeDoCampoNoObjeto><NomeDoCampoAninhado>
     */
    Page<ItemLote> findByLoteId(Long lotePaiId, Pageable pageable);
    

    /**
     * NOVO MÉTODO 1:
     * Busca todos os itens pertencentes a um sub-lote (pelo UUID em String).
     * Usado no bloco catch para marcar todos os itens como erro em caso de falha catastrófica.
     */
    List<ItemLote> findByLoteId(String loteId);

    /**
     * NOVO MÉTODO 2:
     * Busca apenas o primeiro item que encontrar para um determinado sub-lote.
     * É uma forma eficiente de obter o Lote "pai" sem precisar carregar a lista inteira.
     * Retorna um Optional, pois pode não encontrar nenhum item.
     */
    Optional<ItemLote> findFirstByLoteId(String loteId);
    
    /**
     * Conta quantos itens pertencem a um Lote pai específico.
     * O nome "LoteId" instrui o Spring a usar o campo "id" da entidade "lote".
     */
    long countByLote_Id(Long lotePaiId);

    /**
     * Conta quantos itens de um Lote pai específico têm um determinado status.
     * O nome "LoteId" e "Status" instrui o Spring a usar os campos corretos.
     */
    long countByLote_IdAndStatus(Long lotePaiId, StatusProcessamento status);

}
