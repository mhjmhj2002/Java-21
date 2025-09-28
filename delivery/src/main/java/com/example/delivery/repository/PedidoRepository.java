package com.example.delivery.repository;

import com.example.delivery.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Encontra todos os pedidos de um cliente específico
    List<Pedido> findByClienteId(Long clienteId);
}
