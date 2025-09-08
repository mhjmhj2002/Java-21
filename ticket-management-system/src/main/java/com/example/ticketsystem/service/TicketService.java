package com.example.ticketsystem.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticketsystem.entity.StatusTicket;
import com.example.ticketsystem.entity.Ticket;
import com.example.ticketsystem.repository.TicketRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket save(Ticket ticket) {
        // Garantir que a data de criação está definida
        if (ticket.getDataCriacao() == null) {
            ticket.setDataCriacao(LocalDateTime.now());
        }
        
        // Atualizar data de atualização
        ticket.setDataAtualizacao(LocalDateTime.now());
        
        Ticket save = ticketRepository.save(ticket);
        
        log.info("Ticket salvo: {}", save.toString());
        
        return save;
    }

    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> findById(Long id) {
        return ticketRepository.findById(id);
    }

    public List<Ticket> findByClienteId(Long clienteId) {
        return ticketRepository.findByClienteId(clienteId);
    }

    public List<Ticket> findByStatus(StatusTicket status) {
        return ticketRepository.findByStatus(status);
    }

    public Ticket update(Long id, Ticket ticketDetails) {
        return ticketRepository.findById(id)
                .map(ticket -> {
                    ticket.setTitulo(ticketDetails.getTitulo());
                    ticket.setDescricao(ticketDetails.getDescricao());
                    ticket.setStatus(ticketDetails.getStatus());
                    ticket.setCliente(ticketDetails.getCliente());
                    ticket.setDataAtualizacao(LocalDateTime.now());
                    return ticketRepository.save(ticket);
                })
                .orElseThrow(() -> new RuntimeException("Ticket não encontrado com id: " + id));
    }

    public Ticket updateStatus(Long id, StatusTicket status) {
        return ticketRepository.findById(id)
                .map(ticket -> {
                    ticket.setStatus(status);
                    ticket.setDataAtualizacao(LocalDateTime.now());
                    return ticketRepository.save(ticket);
                })
                .orElseThrow(() -> new RuntimeException("Ticket não encontrado com id: " + id));
    }

    public void deleteById(Long id) {
        if (ticketRepository.existsById(id)) {
            ticketRepository.deleteById(id);
        } else {
            throw new RuntimeException("Ticket não encontrado com id: " + id);
        }
    }

    public long count() {
        return ticketRepository.count();
    }

    public long countByStatus(StatusTicket status) {
        return ticketRepository.countByStatus(status);
    }

    public boolean existsById(Long id) {
        return ticketRepository.existsById(id);
    }

    public List<Ticket> searchByTitulo(String titulo) {
        return ticketRepository.findByTituloContainingIgnoreCase(titulo);
    }

    public List<Ticket> findRecentTickets(int limit) {
        return ticketRepository.findTopNByOrderByDataCriacaoDesc(limit);
    }
}