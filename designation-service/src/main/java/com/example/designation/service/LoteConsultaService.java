package com.example.designation.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.designation.aggregator.LoteProgressAggregator;
import com.example.designation.dto.LoteProgressoDTO;
import com.example.designation.entity.Lote;
import com.example.designation.entity.StatusLote;
import com.example.designation.repository.LoteRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class LoteConsultaService {

    private final LoteRepository loteRepository;
    private final LoteProgressAggregator progressAggregator;

    public LoteConsultaService(LoteRepository loteRepository, LoteProgressAggregator progressAggregator) {
        this.loteRepository = loteRepository;
        this.progressAggregator = progressAggregator;
    }

    @Transactional(readOnly = true)
    public LoteProgressoDTO getProgressoDoLote(Long loteId) {
        // 1. Busca sempre a entidade principal no banco de dados para os dados base.
        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado com ID: " + loteId));

        LoteProgressoDTO dto = new LoteProgressoDTO();
        dto.setId(lote.getId());
        dto.setNomeArquivo(lote.getNomeArquivo());
        dto.setStatus(lote.getStatus());
        dto.setDataUpload(lote.getDataUpload());
        dto.setTotalItens(lote.getTotalItens());
        dto.setTotalSubLotes(lote.getTotalSubLotes());

        // 2. Decide a fonte dos contadores: memória (se processando) ou banco (se concluído).
        if (lote.getStatus() == StatusLote.PROCESSANDO || lote.getStatus() == StatusLote.PENDENTE) {
            // Tenta obter o progresso em tempo real do agregador em memória.
            Optional<LoteProgressAggregator.ProgressoLote> progressoOptional = progressAggregator.getProgresso(loteId);

            if (progressoOptional.isPresent()) {
                LoteProgressAggregator.ProgressoLote progressoEmMemoria = progressoOptional.get();
                dto.setItensProcessados(progressoEmMemoria.itensProcessados.get());
                dto.setItensComErro(progressoEmMemoria.itensComErro.get());
                dto.setSubLotesConcluidos(progressoEmMemoria.subLotesConcluidos.get());
            } else {
                // Fallback: se não estiver na memória (ex: app reiniciou), usa os dados do BD.
                // Neste caso, os contadores podem parecer "congelados" até o fim.
                dto.setItensProcessados(0);
                dto.setItensComErro(0);
                dto.setSubLotesConcluidos(0);
            }
        } else {
            // Se o lote já foi finalizado, os dados do banco são a fonte da verdade.
            dto.setItensProcessados(lote.getItensProcessados());
            dto.setItensComErro(lote.getItensComErro());
            dto.setSubLotesConcluidos(lote.getTotalSubLotes());
        }

        // 3. Calcula o progresso percentual.
        if (dto.getTotalItens() > 0) {
            int totalItensContabilizados = dto.getItensProcessados() + dto.getItensComErro();
            dto.setProgressoPercentual((int) (((double) totalItensContabilizados / dto.getTotalItens()) * 100));
        } else {
            dto.setProgressoPercentual(0);
        }

        return dto;
    }
}
