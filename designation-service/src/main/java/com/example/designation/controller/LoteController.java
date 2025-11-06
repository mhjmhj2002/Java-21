package com.example.designation.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.designation.dto.LoteProgressoDTO;
import com.example.designation.entity.ItemLote;
import com.example.designation.entity.Lote;
import com.example.designation.repository.ItemLoteRepository;
import com.example.designation.repository.LoteRepository;
import com.example.designation.service.LoteConsultaService;

@RestController
@RequestMapping("/api/lotes")
public class LoteController {

    private final LoteRepository loteRepository;
    private final ItemLoteRepository itemLoteRepository;
    private final LoteConsultaService loteConsultaService;

	public LoteController(LoteRepository loteRepository, ItemLoteRepository itemLoteRepository, LoteConsultaService loteConsultaService) {
		this.loteRepository = loteRepository;
		this.itemLoteRepository = itemLoteRepository;
		 this.loteConsultaService = loteConsultaService;
	}

    // Endpoint para listar todos os lotes (a grid principal)
    @GetMapping
    public Page<Lote> listarLotes(Pageable pageable) {
        return loteRepository.findAll(pageable);
    }

    // Endpoint para ver os detalhes (itens) de um lote específico
    @GetMapping("/{loteId}/itens")
    public Page<ItemLote> listarItensDoLote(@PathVariable Long loteId, Pageable pageable) {
        return itemLoteRepository.findByLoteId(loteId, pageable);
    }
    
    @GetMapping("/{id}/progresso")
    public ResponseEntity<LoteProgressoDTO> getProgressoDoLote(@PathVariable Long id) {
        LoteProgressoDTO progressoDTO = loteConsultaService.getProgressoDoLote(id);
        return ResponseEntity.ok(progressoDTO);
    }
}
