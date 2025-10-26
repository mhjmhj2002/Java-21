package com.example.designation.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lote_seq_gen")
    @SequenceGenerator(name = "lote_seq_gen", sequenceName = "lote_seq", allocationSize = 1, initialValue = 1000)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String nomeArquivo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataUpload = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusLote status = StatusLote.PENDENTE;
    
    @Column(nullable = false)
    private int totalSubLotes = 0; // Quantos sub-lotes (mensagens) foram gerados para este lote pai

    @Column(nullable = false)
    private int subLotesConcluidos = 0; // Quantos sub-lotes já terminaram de ser processados

    private int totalItens;
    private int itensProcessados;
    private int itensComErro;
}
