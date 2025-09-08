package com.example.ticketsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ticketsystem.entity.StatusTicket;
import com.example.ticketsystem.entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByClienteId(Long clienteId);
    
    List<Ticket> findByStatus(StatusTicket status);
    
    List<Ticket> findByTituloContainingIgnoreCase(String titulo);
    
    long countByStatus(StatusTicket status);
    
    @Query("SELECT t FROM Ticket t ORDER BY t.dataCriacao DESC LIMIT :limit")
    List<Ticket> findTopNByOrderByDataCriacaoDesc(@Param("limit") int limit);
    
    @Query("SELECT t FROM Ticket t WHERE t.cliente.id = :clienteId ORDER BY t.dataCriacao DESC")
    List<Ticket> findLatestByClienteId(@Param("clienteId") Long clienteId);
}