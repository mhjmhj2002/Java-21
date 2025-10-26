package com.example.designation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.designation.dto.FaixaCepInputDTO;
import com.example.designation.dto.FaixaCepResponseDTO;
import com.example.designation.entity.FaixaCep;

@Mapper(componentModel = "spring")
public interface FaixaCepMapper {

    // Mapeia do DTO de entrada para a Entidade
    // O MapStruct é inteligente o suficiente para ignorar o operadorLogisticoId,
    // pois a entidade espera um objeto OperadorLogistico.
    FaixaCep toEntity(FaixaCepInputDTO dto);

    // Mapeia da Entidade para o DTO de Resposta
    @Mapping(source = "operadorLogistico.id", target = "operadorLogisticoId")
    @Mapping(source = "operadorLogistico.nome", target = "nomeOperadorLogistico")
    FaixaCepResponseDTO toResponseDTO(FaixaCep entity);
}
