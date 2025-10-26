package com.example.designation.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.designation.config.RabbitMQConfig;
import com.example.designation.service.LoteProcessingService;

@Component
public class LoteFaixaCepListener {

    private static final Logger log = LoggerFactory.getLogger(LoteFaixaCepListener.class);
    private final LoteProcessingService processingService;

    public LoteFaixaCepListener(LoteProcessingService processingService) {
        this.processingService = processingService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_LOTE_FAIXAS)
    public void onLoteRecebido(String loteId) {
        log.info("Mensagem recebida para processar o lote: {}", loteId);
        try {
            processingService.processarSubLote(loteId);
            log.info("Processamento do lote {} concluído.", loteId);
        } catch (Exception e) {
            log.error("Falha crítica ao processar o lote {}: {}", loteId, e.getMessage());
            // Aqui, poderíamos ter uma lógica para marcar o lote inteiro como falho.
        }
    }
}
