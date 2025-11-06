package com.example.designation.dto;

import com.example.designation.entity.StatusLote;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO que representa o estado em tempo real de um lote em processamento,
 * combinando dados persistidos com os contadores em memória do agregador.
 */
@Data
@NoArgsConstructor
public class LoteProgressoDTO {

    private Long id;
    private String nomeArquivo;
    private StatusLote status;
    private LocalDateTime dataUpload;

    // Contadores que podem vir do banco (se concluído) ou da memória (se processando)
    private int totalItens;
    private int itensProcessados;
    private int itensComErro;
    private int subLotesConcluidos;
    private int totalSubLotes;

    // Campo calculado para o frontend
    private int progressoPercentual;
}
