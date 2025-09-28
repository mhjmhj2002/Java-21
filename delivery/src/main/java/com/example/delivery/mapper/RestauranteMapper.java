package com.example.delivery.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.delivery.dto.RestauranteDTO;
import com.example.delivery.entity.Restaurante;

// @Mapper(componentModel = "spring") informa ao MapStruct para criar um bean Spring,
// permitindo que ele seja injetado em outras classes (como o Service).
@Mapper(componentModel = "spring")
public interface RestauranteMapper {

    // Instância para uso caso a injeção de dependência não seja utilizada.
    RestauranteMapper INSTANCE = Mappers.getMapper(RestauranteMapper.class);

    // Mapeia uma entidade Restaurante para um RestauranteDTO.
    RestauranteDTO toDTO(Restaurante restaurante);

    // Mapeia um RestauranteDTO para uma entidade Restaurante.
    Restaurante toEntity(RestauranteDTO restauranteDTO);

    // Mapeia uma lista de entidades para uma lista de DTOs.
    List<RestauranteDTO> toDTOList(List<Restaurante> restaurantes);
}
