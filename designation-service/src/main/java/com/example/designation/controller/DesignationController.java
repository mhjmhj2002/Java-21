package com.example.designation.controller;

import com.example.designation.repository.FaixaCepRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/designacao" )
public class DesignationController {

    private final FaixaCepRepository faixaCepRepository;

    public DesignationController(FaixaCepRepository faixaCepRepository) {
        this.faixaCepRepository = faixaCepRepository;
    }

    @GetMapping
    public ResponseEntity<?> designarPorCep(@RequestParam String cep) {
        // Remove caracteres não numéricos do CEP
        String cepLimpo = cep.replaceAll("\\D", "");

        return faixaCepRepository.findByCep(cepLimpo)
            .map(faixa -> {
                // Retorna um objeto JSON simples com o nome do operador
                Map<String, String> response = Map.of(
                    "operadorLogistico", faixa.getOperadorLogistico().getNome(),
                    "tipoEntrega", faixa.getTipoEntrega()
                );
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.notFound().build()); // Retorna 404 se nenhum operador atender ao CEP
    }
}
