package com.example.delivery.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.delivery.dto.RestauranteDTO;
import com.example.delivery.entity.Restaurante;
import com.example.delivery.mapper.RestauranteMapper;
import com.example.delivery.repository.RestauranteRepository;
import com.example.delivery.service.RestauranteService;

import jakarta.persistence.EntityNotFoundException;

/**
 * SRP (Single Responsibility Principle):
 * A responsabilidade desta classe é puramente a lógica de negócio para Restaurantes.
 * Ela orquestra o acesso ao repositório e a conversão de dados, mas não lida
 * com requisições HTTP.
 *
 * DIP (Dependency Inversion Principle):
 * Esta classe depende de abstrações (RestauranteRepository, RestauranteMapper),
 * não de implementações concretas. O Spring injeta as dependências via construtor.
 */
@Service
public class RestauranteServiceImpl implements RestauranteService {

    private final RestauranteRepository restauranteRepository;
    private final RestauranteMapper restauranteMapper;

    // Injeção de dependências via construtor é a melhor prática.
    public RestauranteServiceImpl(RestauranteRepository restauranteRepository, RestauranteMapper restauranteMapper) {
        this.restauranteRepository = restauranteRepository;
        this.restauranteMapper = restauranteMapper;
    }

    @Override
    public List<RestauranteDTO> listarTodos() {
        List<Restaurante> restaurantes = restauranteRepository.findAll();
        return restauranteMapper.toDTOList(restaurantes);
    }

    @Override
    public RestauranteDTO buscarPorId(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado com id: " + id)); // Idealmente, uma exceção customizada
        return restauranteMapper.toDTO(restaurante);
    }

    @Override
    public RestauranteDTO criar(RestauranteDTO restauranteDTO) {
        Restaurante restaurante = restauranteMapper.toEntity(restauranteDTO);
        Restaurante restauranteSalvo = restauranteRepository.save(restaurante);
        return restauranteMapper.toDTO(restauranteSalvo);
    }
    
    @Override
    @Transactional
    public RestauranteDTO atualizar(Long id, RestauranteDTO restauranteDTO) {
        Restaurante restauranteExistente = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com id: " + id));

        // Atualiza os dados da entidade com os dados do DTO
        restauranteExistente.setNome(restauranteDTO.nome());
        restauranteExistente.setEndereco(restauranteDTO.endereco());

        Restaurante restauranteAtualizado = restauranteRepository.save(restauranteExistente);
        return restauranteMapper.toDTO(restauranteAtualizado);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        if (!restauranteRepository.existsById(id)) {
            throw new EntityNotFoundException("Restaurante não encontrado com id: " + id);
        }
        restauranteRepository.deleteById(id);
    }
}
