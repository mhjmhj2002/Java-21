package com.example.designation.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.designation.config.RabbitMQConfig;
import com.example.designation.dto.FaixaCepInputDTO;
import com.example.designation.entity.ItemLote;
import com.example.designation.entity.OperadorLogistico;
import com.example.designation.entity.StatusProcessamento;
import com.example.designation.repository.ItemLoteRepository;
import com.example.designation.repository.OperadorLogisticoRepository;

/**
 * Serviço responsável por processar os sub-lotes em segundo plano.
 * Ao final do processamento de um sub-lote, ele publica uma mensagem
 * para a fila de verificação para que o lote pai seja finalizado.
 */
@Service
public class LoteProcessingService {

    private static final Logger log = LoggerFactory.getLogger(LoteProcessingService.class);

    private final ItemLoteRepository itemLoteRepository;
    private final OperadorLogisticoRepository operadorRepository;
    private final FaixaCepService faixaCepService;
    private final RabbitTemplate rabbitTemplate;

    public LoteProcessingService(ItemLoteRepository itemLoteRepository,
                                 OperadorLogisticoRepository operadorRepository,
                                 FaixaCepService faixaCepService,
                                 RabbitTemplate rabbitTemplate) {
        this.itemLoteRepository = itemLoteRepository;
        this.operadorRepository = operadorRepository;
        this.faixaCepService = faixaCepService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public void processarSubLote(String loteId) {
        List<ItemLote> itensPendentes = itemLoteRepository.findByLoteIdAndStatus(loteId, StatusProcessamento.PENDENTE);
        if (itensPendentes.isEmpty()) {
            log.warn("Nenhum item pendente encontrado para o loteId: {}. Nenhum processamento necessário.", loteId);
            return;
        }

        final Long lotePaiId = itensPendentes.get(0).getLote().getId();

        for (ItemLote item : itensPendentes) {
            processarRegistro(item);
        }
        
        log.info("Processamento do sub-lote {} concluído.", loteId);

        // Publica uma mensagem para a fila de verificação.
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_PRINCIPAL, RabbitMQConfig.QUEUE_VERIFICACAO_LOTE, lotePaiId);
    }

    private void processarRegistro(ItemLote item) {
        try {
            OperadorLogistico operador = operadorRepository
                .findByNome(item.getOperadorLogisticoNome())
                .orElseThrow(() -> new IllegalArgumentException("Operador logístico não encontrado: " + item.getOperadorLogisticoNome()));

            FaixaCepInputDTO dto = new FaixaCepInputDTO();
            dto.setCepInicial(item.getCepInicial());
            dto.setCepFinal(item.getCepFinal());
            dto.setCidade(item.getCidade());
            dto.setUf(item.getUf());
            dto.setTipoEntrega(item.getTipoEntrega());
            dto.setOperadorLogisticoId(operador.getId());

            faixaCepService.criar(dto);

            item.setStatus(StatusProcessamento.PROCESSADO);
            itemLoteRepository.save(item);
        } catch (Exception e) {
        	item.setStatus(StatusProcessamento.ERRO);
            item.setMensagemErro(e.getMessage().substring(0, Math.min(e.getMessage().length(), 512)));
            itemLoteRepository.save(item);
        }
    }
}
