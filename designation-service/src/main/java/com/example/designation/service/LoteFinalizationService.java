package com.example.designation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.designation.entity.Lote;
import com.example.designation.entity.StatusLote;
import com.example.designation.entity.StatusProcessamento;
import com.example.designation.repository.ItemLoteRepository;
import com.example.designation.repository.LoteRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * Serviço dedicado com a única responsabilidade de verificar e finalizar
 * o processamento de um Lote. É projetado para ser chamado em uma nova
 * transação para garantir a leitura de dados já commitados.
 */
@Service
public class LoteFinalizationService {

	private static final Logger log = LoggerFactory.getLogger(LoteFinalizationService.class);
    private final LoteRepository loteRepository;
    private final ItemLoteRepository itemLoteRepository; // INJETAR

    public LoteFinalizationService(LoteRepository loteRepository, ItemLoteRepository itemLoteRepository) { // Adicionar
        this.loteRepository = loteRepository;
        this.itemLoteRepository = itemLoteRepository; // Atribuir
    }

    /**
     * Verifica se um lote atingiu as condições de finalização.
     * Esta é a versão final e robusta contra concorrência.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void verificarEFinalizar(Long loteId) {
        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new EntityNotFoundException("Lote " + loteId + " não encontrado."));

        // Conta quantos itens já foram processados (com sucesso ou erro)
        long itensJaProcessados = itemLoteRepository.countByLote_Id(loteId);
        
        log.info("Verificação para Lote {}: {}/{} itens já tiveram seu status final definido.", 
            loteId, itensJaProcessados, lote.getTotalItens());

        // A condição de finalização agora é baseada no total de itens, não de sub-lotes.
        if (itensJaProcessados >= lote.getTotalItens()) {
            log.info("Todos os itens do Lote {} foram processados. Consolidando resultados...", loteId);
            
            // Conta os resultados finais
            long totalErros = itemLoteRepository.countByLote_IdAndStatus(loteId, StatusProcessamento.ERRO);
            long totalSucessos = itensJaProcessados - totalErros;
            
            // Atualiza o Lote pai de uma só vez
            lote.setItensProcessados((int) totalSucessos);
            lote.setItensComErro((int) totalErros);
            lote.setSubLotesConcluidos(lote.getTotalSubLotes()); // Marca todos como concluídos
            
            StatusLote statusFinal = totalErros > 0 ? StatusLote.FALHA_PARCIAL : StatusLote.CONCLUIDO;
            lote.setStatus(statusFinal);
            
            loteRepository.save(lote); // Salva o estado final consolidado
            
            log.info("LOTE {} FINALIZADO. Status: {}. Sucessos: {}, Erros: {}", 
                loteId, statusFinal, totalSucessos, totalErros);
        }
    }
}
