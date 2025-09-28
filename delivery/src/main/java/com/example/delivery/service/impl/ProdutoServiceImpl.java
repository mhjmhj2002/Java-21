package com.example.delivery.service.impl;

import com.example.delivery.dto.ProdutoInputDTO;
import com.example.delivery.dto.ProdutoResponseDTO;
import com.example.delivery.entity.Produto;
import com.example.delivery.entity.Restaurante;
import com.example.delivery.mapper.ProdutoMapper;
import com.example.delivery.repository.ProdutoRepository;
import com.example.delivery.repository.RestauranteRepository;
import com.example.delivery.service.ProdutoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SRP (Single Responsibility Principle):
 * Responsável pela lógica de negócio de Produtos, incluindo a associação
 * com um Restaurante.
 */
@Service
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoMapper produtoMapper;

    // DIP: Injetando abstrações via construtor.
    public ProdutoServiceImpl(ProdutoRepository produtoRepository, RestauranteRepository restauranteRepository, ProdutoMapper produtoMapper) {
        this.produtoRepository = produtoRepository;
        this.restauranteRepository = restauranteRepository;
        this.produtoMapper = produtoMapper;
    }

    @Override
    public List<ProdutoResponseDTO> listarPorRestaurante(Long restauranteId) {
        // Verifica se o restaurante existe antes de listar os produtos.
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new EntityNotFoundException("Restaurante não encontrado com id: " + restauranteId);
        }
        List<Produto> produtos = produtoRepository.findByRestauranteId(restauranteId);
        return produtoMapper.toResponseDTOList(produtos);
    }

    @Override
    @Transactional
    public ProdutoResponseDTO criar(Long restauranteId, ProdutoInputDTO produtoInputDTO) {
        // Busca o restaurante ao qual o produto será associado.
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com id: " + restauranteId));

        Produto produto = produtoMapper.toEntity(produtoInputDTO);
        produto.setRestaurante(restaurante); // Associa o produto ao restaurante.

        Produto produtoSalvo = produtoRepository.save(produto);
        return produtoMapper.toResponseDTO(produtoSalvo);
    }

    @Override
    @Transactional
    public ProdutoResponseDTO atualizar(Long produtoId, ProdutoInputDTO produtoInputDTO) {
        Produto produtoExistente = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com id: " + produtoId));

        // Atualiza os campos do produto existente.
        produtoExistente.setNome(produtoInputDTO.nome());
        produtoExistente.setDescricao(produtoInputDTO.descricao());
        produtoExistente.setPreco(produtoInputDTO.preco());

        Produto produtoAtualizado = produtoRepository.save(produtoExistente);
        return produtoMapper.toResponseDTO(produtoAtualizado);
    }

    @Override
    @Transactional
    public void deletar(Long produtoId) {
        if (!produtoRepository.existsById(produtoId)) {
            throw new EntityNotFoundException("Produto não encontrado com id: " + produtoId);
        }
        produtoRepository.deleteById(produtoId);
    }
}
