package com.example.designation.service;

import com.example.designation.dto.FaixaCepInputDTO;
import com.example.designation.dto.FaixaCepResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FaixaCepService {
    FaixaCepResponseDTO criar(FaixaCepInputDTO dto);
    Page<FaixaCepResponseDTO> listar(Pageable pageable);
    FaixaCepResponseDTO buscarPorId(Long id);
    FaixaCepResponseDTO atualizar(Long id, FaixaCepInputDTO dto);
    void deletar(Long id);
}
