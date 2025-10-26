package com.example.designation.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.designation.entity.ItemLote;
import com.example.designation.entity.Lote;
import com.example.designation.repository.ItemLoteRepository;
import com.example.designation.repository.LoteRepository;

@RestController
@RequestMapping("/api/lotes")
public class LoteController {

    private final LoteRepository loteRepository;
    private final ItemLoteRepository itemLoteRepository;

	public LoteController(LoteRepository loteRepository, ItemLoteRepository itemLoteRepository) {
		this.loteRepository = loteRepository;
		this.itemLoteRepository = itemLoteRepository;
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
}
