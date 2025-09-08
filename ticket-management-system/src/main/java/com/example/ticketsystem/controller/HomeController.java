package com.example.ticketsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.ticketsystem.entity.StatusTicket;
import com.example.ticketsystem.service.BatchService;
import com.example.ticketsystem.service.ClienteService;
import com.example.ticketsystem.service.TicketService;

@Controller
public class HomeController {

    private final ClienteService clienteService;
    private final TicketService ticketService;
    private final BatchService batchService;

    @Autowired
    public HomeController(ClienteService clienteService, TicketService ticketService, BatchService batchService) {
        this.clienteService = clienteService;
        this.ticketService = ticketService;
        this.batchService = batchService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalClientes", clienteService.count());
        model.addAttribute("totalTickets", ticketService.count());
        model.addAttribute("ticketsAbertos", ticketService.countByStatus(StatusTicket.ABERTO));
        model.addAttribute("recentProcesses", batchService.getRecentProcesses(5));
        return "index";
    }
}