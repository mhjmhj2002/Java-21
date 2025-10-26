package com.example.designation.dto;

import lombok.Data;

@Data
public class FaixaCepInputDTO {
    private String cepInicial;
    private String cepFinal;
    private String cidade;
    private String uf;
    private String tipoEntrega;
    private Long operadorLogisticoId; // Apenas o ID do operador
}
