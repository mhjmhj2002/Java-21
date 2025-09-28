package com.example.delivery.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.delivery.dto.ProdutoInputDTO;
import com.example.delivery.dto.ProdutoResponseDTO;
import com.example.delivery.entity.Produto;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    // Converte a Entidade para o DTO de Resposta.
    // O MapStruct entende que 'restaurante.id' deve ser mapeado para 'restauranteId'.
    @Mapping(source = "restaurante.id", target = "restauranteId")
    ProdutoResponseDTO toResponseDTO(Produto produto);

    // Converte o DTO de Entrada para a Entidade.
    Produto toEntity(ProdutoInputDTO produtoInputDTO);

    // Converte uma lista de Entidades para uma lista de DTOs de Resposta.
    List<ProdutoResponseDTO> toResponseDTOList(List<Produto> produtos);
}
