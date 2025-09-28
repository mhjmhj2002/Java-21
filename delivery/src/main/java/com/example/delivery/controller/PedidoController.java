package com.example.delivery.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.delivery.service.PedidoService;

//LSP: Graças à injeção da interface PedidoService, qualquer classe que a implemente
//pode ser injetada aqui sem quebrar o funcionamento do controller,
//desde que respeite o contrato da interface.
@RestController
public class PedidoController {
 private final PedidoService pedidoService;

 public PedidoController(PedidoService pedidoService) {
     this.pedidoService = pedidoService;
 }
}

