package com.example.delivery.service;

import com.example.delivery.dto.ClienteDTO;
import com.example.delivery.dto.ClienteInputDTO;
import java.util.List;

/**
 * OCP & DIP: Contrato para a lógica de negócio de Cliente.
 */
public interface ClienteService {
    List<ClienteDTO> listarTodos();
    ClienteDTO buscarPorId(Long id);
    ClienteDTO criar(ClienteInputDTO clienteInputDTO);
    ClienteDTO atualizar(Long id, ClienteInputDTO clienteInputDTO);
    void deletar(Long id);
}
