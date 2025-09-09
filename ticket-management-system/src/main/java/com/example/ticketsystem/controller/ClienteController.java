package com.example.ticketsystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ticketsystem.entity.Cliente;
import com.example.ticketsystem.service.ClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listClientes(Model model) {
        List<Cliente> clientes = clienteService.findAll();
        model.addAttribute("clientes", clientes);
        return "clientes/list";
    }

    @GetMapping("/novo")
    public String novoClienteForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/form";
    }

    @PostMapping
    public String criarCliente(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
        try {
            clienteService.save(cliente);
            redirectAttributes.addFlashAttribute("success", "Cliente criado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao criar cliente: " + e.getMessage());
        }
        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    public String editarClienteForm(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        model.addAttribute("cliente", cliente);
        return "clientes/form";
    }

    @PostMapping("/editar/{id}")
    public String atualizarCliente(@PathVariable Long id, @ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
        try {
            clienteService.update(id, cliente);
            redirectAttributes.addFlashAttribute("success", "Cliente atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar cliente: " + e.getMessage());
        }
        return "redirect:/clientes";
    }

    @GetMapping("/excluir/{id}")
    public String excluirCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Cliente excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir cliente: " + e.getMessage());
        }
        return "redirect:/clientes";
    }
}