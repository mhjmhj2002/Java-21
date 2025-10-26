package com.example.designation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class FaixaCep {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "faixa_cep_seq_gen")
    @SequenceGenerator(name = "faixa_cep_seq_gen", sequenceName = "faixa_cep_seq", allocationSize = 1, initialValue = 1000)
    private Long id;

    @Column(nullable = false)
    private String cepInicial; // Ex: "20000000"

    @Column(nullable = false)
    private String cepFinal;   // Ex: "22999999"

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String uf;

    @Column(nullable = false)
    private String tipoEntrega; // Ex: "Expresso", "Normal"

    @ManyToOne(optional = false)
    @JoinColumn(name = "operador_logistico_id")
    private OperadorLogistico operadorLogistico;
}
