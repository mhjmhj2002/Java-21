package com.example.ticketsystem.mapper;

import com.example.ticketsystem.dto.TicketRequestDTO;
import com.example.ticketsystem.dto.TicketResponseDTO;
import com.example.ticketsystem.entity.Ticket;
import com.example.ticketsystem.entity.StatusTicket;
import com.example.ticketsystem.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    private final ClienteService clienteService;

    @Autowired
    public TicketMapper(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    public Ticket toEntity(TicketRequestDTO dto) {
        Ticket ticket = new Ticket();
        ticket.setTitulo(dto.getTitulo());
        ticket.setDescricao(dto.getDescricao());
        ticket.setStatus(convertStringToStatusTicket(dto.getStatus()));
        ticket.setCliente(clienteService.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado")));
        return ticket;
    }

    public TicketResponseDTO toDto(Ticket ticket) {
        return new TicketResponseDTO(
                ticket.getId(),
                ticket.getTitulo(),
                ticket.getDescricao(),
                ticket.getStatus(),
                ticket.getCliente().getId(),
                ticket.getCliente().getNome(),
                ticket.getDataCriacao(),
                ticket.getDataAtualizacao()
        );
    }

    public void updateEntityFromDto(TicketRequestDTO dto, Ticket ticket) {
        ticket.setTitulo(dto.getTitulo());
        ticket.setDescricao(dto.getDescricao());
        ticket.setStatus(convertStringToStatusTicket(dto.getStatus()));
        ticket.setCliente(clienteService.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado")));
    }

    private StatusTicket convertStringToStatusTicket(String statusString) {
        if (statusString == null) {
            return StatusTicket.ABERTO; // Valor padrão
        }

        // Primeiro tenta converter pelo nome do enum
        try {
            return StatusTicket.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Se não encontrar pelo nome, busca pela descrição
            for (StatusTicket status : StatusTicket.values()) {
                if (status.getDescricao().equalsIgnoreCase(statusString)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Status inválido: " + statusString);
        }
    }
}