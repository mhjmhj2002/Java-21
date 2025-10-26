package com.example.designation.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.designation.service.LoteUploadService;

@RestController
@RequestMapping("/api/lotes" )
public class LoteUploadController {

    private final LoteUploadService loteUploadService;

    public LoteUploadController(LoteUploadService loteUploadService) {
        this.loteUploadService = loteUploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadLote(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "O arquivo não pode estar vazio."));
        }

        try {
            loteUploadService.processarUpload(file);
            // Retorna HTTP 202 Accepted, indicando que a requisição foi aceita para processamento.
            return ResponseEntity.accepted().body(Map.of(
                "message", "Arquivo recebido. O processamento foi iniciado em segundo plano."
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Falha ao processar o arquivo: " + e.getMessage()));
        }
    }
}
