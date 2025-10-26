package com.example.designation.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.designation.dto.FaixaCepInputDTO;
import com.example.designation.dto.FaixaCepResponseDTO;
import com.example.designation.service.FaixaCepService;

@RestController
@RequestMapping("/api/faixas-cep" )
public class FaixaCepController {

    private final FaixaCepService faixaCepService;

    public FaixaCepController(FaixaCepService faixaCepService) {
        this.faixaCepService = faixaCepService;
    }

    @PostMapping
    public ResponseEntity<FaixaCepResponseDTO> criar(@RequestBody FaixaCepInputDTO dto) {
        FaixaCepResponseDTO novaFaixa = faixaCepService.criar(dto);
        return new ResponseEntity<>(novaFaixa, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<FaixaCepResponseDTO>> listar(Pageable pageable) {
        Page<FaixaCepResponseDTO> paginaDeFaixas = faixaCepService.listar(pageable);
        return ResponseEntity.ok(paginaDeFaixas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FaixaCepResponseDTO> buscarPorId(@PathVariable Long id) {
        FaixaCepResponseDTO faixa = faixaCepService.buscarPorId(id);
        return ResponseEntity.ok(faixa);
    }

    /**
     * Atualiza uma faixa de CEP existente.
     * Recebe o ID na URL e os novos dados no corpo da requisição.
     * Delega toda a lógica de validação e atualização para a camada de serviço.
     *
     * @param id O ID da Faixa de CEP a ser atualizada.
     * @param dto O DTO com os novos dados para a faixa.
     * @return Um ResponseEntity com o DTO da faixa atualizada e status 200 OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FaixaCepResponseDTO> atualizar(@PathVariable Long id, @RequestBody FaixaCepInputDTO dto) {
        FaixaCepResponseDTO faixaAtualizada = faixaCepService.atualizar(id, dto);
        return ResponseEntity.ok(faixaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        faixaCepService.deletar(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}
