package com.example.delivery.specification;

import com.example.delivery.entity.Pedido;
import com.example.delivery.enums.StatusPedido;
import org.springframework.data.jpa.domain.Specification;

public class PedidoSpecification {

    public static Specification<Pedido> comFiltros(Long clienteId, Long restauranteId, StatusPedido status) {
        return Specification.where(comCliente(clienteId))
                .and(comRestaurante(restauranteId))
                .and(comStatus(status));
    }

    private static Specification<Pedido> comCliente(Long clienteId) {
        if (clienteId == null) {
            return null; // Não aplica o filtro se o ID for nulo
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("cliente").get("id"), clienteId);
    }

    private static Specification<Pedido> comRestaurante(Long restauranteId) {
        if (restauranteId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("restaurante").get("id"), restauranteId);
    }

    private static Specification<Pedido> comStatus(StatusPedido status) {
        if (status == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }
}
