package com.example.delivery.service.impl;

import com.example.delivery.dto.ClienteDTO;
import com.example.delivery.dto.ClienteInputDTO;
import com.example.delivery.entity.Cliente;
import com.example.delivery.mapper.ClienteMapper;
import com.example.delivery.repository.ClienteRepository;
import com.example.delivery.service.ClienteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SRP: Responsável pela lógica de negócio de Clientes.
 */
@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    // DIP: Injetando abstrações via construtor.
    public ClienteServiceImpl(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    @Override
    public List<ClienteDTO> listarTodos() {
        return clienteMapper.toDTOList(clienteRepository.findAll());
    }

    @Override
    public ClienteDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com id: " + id));
        return clienteMapper.toDTO(cliente);
    }

    @Override
    @Transactional
    public ClienteDTO criar(ClienteInputDTO clienteInputDTO) {
        // Regra de negócio: não permitir criar clientes com e-mail duplicado.
        clienteRepository.findByEmail(clienteInputDTO.email()).ifPresent(c -> {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        });

        Cliente cliente = clienteMapper.toEntity(clienteInputDTO);
        Cliente clienteSalvo = clienteRepository.save(cliente);
        return clienteMapper.toDTO(clienteSalvo);
    }

    @Override
    @Transactional
    public ClienteDTO atualizar(Long id, ClienteInputDTO clienteInputDTO) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com id: " + id));

        clienteExistente.setNome(clienteInputDTO.nome());
        clienteExistente.setEmail(clienteInputDTO.email());
        clienteExistente.setTelefone(clienteInputDTO.telefone());

        Cliente clienteAtualizado = clienteRepository.save(clienteExistente);
        return clienteMapper.toDTO(clienteAtualizado);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new EntityNotFoundException("Cliente não encontrado com id: " + id);
        }
        clienteRepository.deleteById(id);
    }
}
