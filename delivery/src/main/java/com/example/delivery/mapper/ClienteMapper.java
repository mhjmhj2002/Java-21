package com.example.delivery.mapper;

import com.example.delivery.dto.ClienteDTO;
import com.example.delivery.dto.ClienteInputDTO;
import com.example.delivery.entity.Cliente;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteDTO toDTO(Cliente cliente);

    Cliente toEntity(ClienteInputDTO clienteInputDTO);

    List<ClienteDTO> toDTOList(List<Cliente> clientes);
}
