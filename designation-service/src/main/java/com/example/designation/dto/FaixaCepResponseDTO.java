package com.example.designation.dto;

import lombok.Data;

@Data
public class FaixaCepResponseDTO {
    private Long id;
    private String cepInicial;
    private String cepFinal;
    private String cidade;
    private String uf;
    private String tipoEntrega;
    private Long operadorLogisticoId;
    private String nomeOperadorLogistico; // Campo extra para conveniência
}
