package com.example.designation.aggregator;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.designation.entity.Lote;
import com.example.designation.entity.StatusLote;
import com.example.designation.repository.LoteRepository;

/**
 * Padrão Singleton (gerenciado pelo Spring como @Component) e Agregador de Eventos.
 * Mantém o progresso dos lotes em memória para evitar contenção de lock no banco de dados.
 * É thread-safe.
 */
@Component
public class LoteProgressAggregator {

    private static final Logger log = LoggerFactory.getLogger(LoteProgressAggregator.class);

    // Estrutura para guardar o progresso de cada lote em processamento.
    // Key: Lote ID, Value: Objeto de progresso
    private final Map<Long, ProgressoLote> progressoMap = new ConcurrentHashMap<>();

    private final LoteRepository loteRepository;

    public LoteProgressAggregator(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    /**
     * Inicia o rastreamento de um novo lote.
     */
    public void iniciarLote(Lote lote) {
        progressoMap.put(lote.getId(), new ProgressoLote(lote.getTotalSubLotes()));
        log.info("Agregador: Iniciando rastreamento para o Lote ID: {}", lote.getId());
    }

    /**
     * Método sincronizado para atualizar o progresso.
     * Recebe o resultado de um sub-lote e decide se o lote principal terminou.
     */
    public synchronized void registrarProgresso(Long loteId, int sucessos, int erros) {
        ProgressoLote progresso = progressoMap.get(loteId);
        if (progresso == null) {
            log.warn("Agregador: Recebido progresso para o Lote ID: {}, mas ele não está sendo rastreado.", loteId);
            return;
        }

        // Atualiza os contadores em memória
        progresso.itensProcessados.addAndGet(sucessos);
        progresso.itensComErro.addAndGet(erros);
        int subLotesConcluidos = progresso.subLotesConcluidos.incrementAndGet();

        log.info("Agregador: Progresso registrado para Lote ID: {}. Sub-lotes concluídos: {}/{}",
                loteId, subLotesConcluidos, progresso.totalSubLotes);

        // Verifica se o processamento terminou
        if (subLotesConcluidos >= progresso.totalSubLotes) {
            log.info("Agregador: CONDIÇÃO DE FINALIZAÇÃO ATINGIDA para o Lote ID: {}", loteId);
            finalizarLoteNoBanco(loteId, progresso);
        }
    }

    /**
     * Método privado para persistir o resultado final no banco de dados.
     * É chamado UMA ÚNICA VEZ quando o lote termina.
     */
    private void finalizarLoteNoBanco(Long loteId, ProgressoLote progresso) {
        try {
            Lote lote = loteRepository.findById(loteId).orElseThrow();

            lote.setItensProcessados(progresso.itensProcessados.get());
            lote.setItensComErro(progresso.itensComErro.get());
            lote.setSubLotesConcluidos(progresso.subLotesConcluidos.get());

            StatusLote statusFinal = progresso.itensComErro.get() > 0 ? StatusLote.FALHA_PARCIAL : StatusLote.CONCLUIDO;
            lote.setStatus(statusFinal);

            loteRepository.save(lote);

            log.info("Agregador: LOTE {} FINALIZADO E SALVO NO BANCO. Status: {}", loteId, statusFinal);

        } catch (Exception e) {
            log.error("Agregador: FALHA CRÍTICA ao tentar finalizar o Lote ID: {} no banco de dados.", loteId, e);
        } finally {
            // Limpa o lote da memória para não consumir recursos indefinidamente
            progressoMap.remove(loteId);
            log.info("Agregador: Lote ID: {} removido da memória.", loteId);
        }
    }
    
    /**
     * NOVO MÉTODO: Retorna o estado atual do progresso de um lote em memória.
     * Usado pelo serviço de consulta para fornecer dados em tempo real.
     * @return um Optional contendo o progresso, ou vazio se o lote não estiver sendo rastreado.
     */
    public Optional<ProgressoLote> getProgresso(Long loteId) {
        return Optional.ofNullable(progressoMap.get(loteId));
    }

    /**
     * A classe interna ProgressoLote precisa ser pública (ou pelo menos visível)
     * para que outros serviços possam usá-la. Vamos torná-la pública e estática.
     */
    public static class ProgressoLote {
        public final int totalSubLotes;
        public volatile AtomicInteger subLotesConcluidos = new AtomicInteger(0);
        public volatile AtomicInteger itensProcessados = new AtomicInteger(0);
        public volatile AtomicInteger itensComErro = new AtomicInteger(0);

        public ProgressoLote(int totalSubLotes) {
            this.totalSubLotes = totalSubLotes;
        }
    }
    
    
}
