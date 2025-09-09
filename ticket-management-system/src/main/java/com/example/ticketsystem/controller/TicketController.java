package com.example.ticketsystem.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ticketsystem.dto.TicketRequestDTO;
import com.example.ticketsystem.entity.StatusTicket;
import com.example.ticketsystem.entity.Ticket;
import com.example.ticketsystem.mapper.TicketMapper;
import com.example.ticketsystem.service.ClienteService;
import com.example.ticketsystem.service.TicketService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final ClienteService clienteService;
    private final TicketMapper ticketMapper;

    @Autowired
    public TicketController(TicketService ticketService, ClienteService clienteService, TicketMapper ticketMapper) {
        this.ticketService = ticketService;
        this.clienteService = clienteService;
        this.ticketMapper = ticketMapper;
    }

    @GetMapping
    public String listTickets(Model model) {
        List<Ticket> tickets = ticketService.findAll();
        model.addAttribute("tickets", tickets);
        return "tickets/list";
    }

    @GetMapping("/novo")
    public String novoTicketForm(Model model) {
        model.addAttribute("ticketDTO", new TicketRequestDTO());
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("statusValues", Arrays.asList(StatusTicket.values()));
        return "tickets/form";
    }

    @PostMapping
    public String criarTicket(@Valid @ModelAttribute("ticketDTO") TicketRequestDTO ticketDTO, 
                             BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("statusValues", Arrays.asList(StatusTicket.values()));
            return "tickets/form";
        }

        try {
            Ticket ticket = ticketMapper.toEntity(ticketDTO);
            ticketService.save(ticket);
            redirectAttributes.addFlashAttribute("success", "Ticket criado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao criar ticket: " + e.getMessage());
        }
        return "redirect:/tickets";
    }

    @GetMapping("/editar/{id}")
    public String editarTicketForm(@PathVariable Long id, Model model) {
        Ticket ticket = ticketService.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket não encontrado"));
        
        // Converter Entity para DTO para o formulário
        TicketRequestDTO ticketDTO = new TicketRequestDTO();
        ticketDTO.setTitulo(ticket.getTitulo());
        ticketDTO.setDescricao(ticket.getDescricao());
        ticketDTO.setStatus(ticket.getStatus().name()); // Usar o nome do enum
        ticketDTO.setClienteId(ticket.getCliente().getId());
        
        model.addAttribute("ticketDTO", ticketDTO);
        model.addAttribute("id", id); // ← Adicionar o ID para o template
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("statusValues", Arrays.asList(StatusTicket.values()));
        return "tickets/form";
    }

    @PostMapping("/editar/{id}")
    public String atualizarTicket(@PathVariable Long id, 
                                 @Valid @ModelAttribute("ticketDTO") TicketRequestDTO ticketDTO,
                                 BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("statusValues", Arrays.asList(StatusTicket.values()));
            return "tickets/form";
        }

        try {
            Ticket ticket = ticketService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ticket não encontrado"));
            
            ticketMapper.updateEntityFromDto(ticketDTO, ticket);
            ticketService.update(id, ticket);
            redirectAttributes.addFlashAttribute("success", "Ticket atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar ticket: " + e.getMessage());
        }
        return "redirect:/tickets";
    }

    @DeleteMapping("/{id}")
    public String excluirTicket(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ticketService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Ticket excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir ticket: " + e.getMessage());
        }
        return "redirect:/tickets";
    }
    
    @GetMapping("/visualizar/{id}")
    public String visualizarTicket(@PathVariable Long id, Model model) {
        try {
            Ticket ticket = ticketService.findById(id).get();
            model.addAttribute("ticket", ticket);
            return "tickets/view"; // ← Alterado para "tickets/view" (sem a extensão .html)
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket não encontrado");
        }
    }
}