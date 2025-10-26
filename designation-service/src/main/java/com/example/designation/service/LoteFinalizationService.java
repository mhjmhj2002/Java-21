package com.example.designation.service;

import com.example.designation.entity.Lote;
import com.example.designation.entity.StatusLote;
import com.example.designation.repository.LoteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço dedicado com a única responsabilidade de verificar e finalizar
 * o processamento de um Lote. É projetado para ser chamado em uma nova
 * transação para garantir a leitura de dados já commitados.
 */
@Service
public class LoteFinalizationService {

    private static final Logger log = LoggerFactory.getLogger(LoteFinalizationService.class);
    private final LoteRepository loteRepository;

    public LoteFinalizationService(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    /**
     * Verifica se um lote atingiu as condições de finalização e atualiza seu status.
     * A anotação REQUIRES_NEW garante que este método execute em uma nova transação,
     * lendo o estado mais recente e commitado do banco de dados.
     *
     * @param loteId O ID do lote a ser verificado.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void verificarEFinalizar(Long loteId) {
        log.debug("Iniciando verificação de finalização para o Lote ID: {}", loteId);

        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new EntityNotFoundException("Lote " + loteId + " não encontrado para finalização."));

        // Proteção para evitar reprocessamento ou condições de corrida
        if (lote.getStatus() != StatusLote.PROCESSANDO) {
            log.warn("Lote {} já foi finalizado ou está em estado inesperado ({}). Nenhuma ação necessária.", loteId, lote.getStatus());
            return;
        }

        // A condição de finalização
        if (lote.getSubLotesConcluidos() >= lote.getTotalSubLotes()) {
            log.info("Condição de finalização atendida para o Lote {}. Sub-lotes concluídos: {}/{}",
                    loteId, lote.getSubLotesConcluidos(), lote.getTotalSubLotes());

            StatusLote statusFinal = lote.getItensComErro() > 0 ? StatusLote.FALHA_PARCIAL : StatusLote.CONCLUIDO;

            // Usa a query de atualização explícita para garantir a mudança de status
            loteRepository.atualizarStatus(lote.getId(), statusFinal);
            
            log.info("LOTE {} FINALIZADO. Novo status: {}", lote.getId(), statusFinal);
        } else {
            log.debug("Condição de finalização NÃO atendida para o Lote {}. Sub-lotes concluídos: {}/{}",
                    loteId, lote.getSubLotesConcluidos(), lote.getTotalSubLotes());
        }
    }
}
