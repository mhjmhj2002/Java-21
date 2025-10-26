package com.example.designation.listener;

import com.example.designation.config.RabbitMQConfig;
import com.example.designation.service.LoteFinalizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listener dedicado a consumir mensagens da fila de verificação de lotes.
 * Sua única função é delegar a tarefa de finalização para o LoteFinalizationService.
 */
@Component
public class LoteFinalizationListener {

    private static final Logger log = LoggerFactory.getLogger(LoteFinalizationListener.class);
    private final LoteFinalizationService loteFinalizationService;

    public LoteFinalizationListener(LoteFinalizationService loteFinalizationService) {
        this.loteFinalizationService = loteFinalizationService;
    }

    /**
     * Ouve a fila de verificação. Cada mensagem contém o ID do lote pai a ser verificado.
     * @param lotePaiId O ID do lote pai.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_VERIFICACAO_LOTE)
    public void onVerificacaoRecebida(Long lotePaiId) {
        log.info("Mensagem de verificação recebida para o Lote ID: {}", lotePaiId);
        try {
            // A chamada a este método iniciará uma nova transação.
            loteFinalizationService.verificarEFinalizar(lotePaiId);
            log.info("Verificação para o Lote ID: {} concluída com sucesso.", lotePaiId);
        } catch (Exception e) {
            log.error("Falha CRÍTICA ao verificar a finalização do Lote ID {}: {}", lotePaiId, e.getMessage(), e);
            // Em um cenário de produção, aqui você poderia reenfileirar a mensagem
            // com um contador de tentativas ou enviá-la para uma DLQ específica de verificação.
        }
    }
}
