package com.example.designation.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Representa uma única linha da planilha de importação (um item de um lote).
 */
@Entity
@Data
public class ItemLote {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq_gen")
    @SequenceGenerator(name = "global_seq_gen", sequenceName = "hibernate_sequence", allocationSize = 1, initialValue = 1000)
    private Long id;

    /**
     * Relacionamento com a entidade Lote "pai".
     * Isso nos permite saber a qual arquivo de upload este item pertence.
     * É usado para rastreabilidade e para exibir os detalhes na interface do usuário.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lote_pai_id", nullable = false) // Nome da coluna para clareza
    private Lote lote;

    /**
     * O ID do sub-lote de processamento (um UUID em formato String).
     * Todos os 500 itens processados juntos em uma transação terão o mesmo loteId.
     * Este campo é usado pelo worker (LoteProcessingService) para buscar o trabalho a ser feito.
     * Adicionamos um índice para otimizar as buscas por este campo.
     */
    @Column(nullable = false, updatable = false, length = 36)
    @org.hibernate.annotations.Index(name = "idx_itemlote_loteid") // Opcional, mas bom para performance
    private String loteId;

    // ===================================================================
    // DADOS CRUS DA PLANILHA
    // ===================================================================
    private String cepInicial;
    private String cepFinal;
    private String cidade;
    private String uf;
    private String tipoEntrega;
    private String operadorLogisticoNome;

    // ===================================================================
    // CONTROLE DE PROCESSAMENTO
    // ===================================================================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProcessamento status = StatusProcessamento.PENDENTE;

    @Column(length = 512)
    private String mensagemErro;
}
