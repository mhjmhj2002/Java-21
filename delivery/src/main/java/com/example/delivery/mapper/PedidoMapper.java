package com.example.delivery.mapper;

import com.example.delivery.dto.PedidoResponseDTO;
import com.example.delivery.dto.ItemPedidoResponseDTO;
import com.example.delivery.entity.Pedido;
import com.example.delivery.entity.ItemPedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    // Mapeamentos para converter Pedido (Entity) para PedidoResponseDTO (Saída)
    @Mappings({
        @Mapping(source = "cliente.id", target = "clienteId"),
        @Mapping(source = "cliente.nome", target = "nomeCliente"),
        @Mapping(source = "restaurante.id", target = "restauranteId"),
        @Mapping(source = "restaurante.nome", target = "nomeRestaurante")
    })
    PedidoResponseDTO toResponseDTO(Pedido pedido);

    List<PedidoResponseDTO> toResponseDTOList(List<Pedido> pedidos);

    // Mapeamentos para converter ItemPedido (Entity) para ItemPedidoResponseDTO (Saída)
    @Mappings({
        @Mapping(source = "produto.id", target = "produtoId"),
        @Mapping(source = "produto.nome", target = "nomeProduto")
    })
    ItemPedidoResponseDTO itemToResponseDTO(ItemPedido itemPedido);
}
