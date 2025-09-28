package com.example.delivery.service.impl;

import org.springframework.stereotype.Service;

import com.example.delivery.service.PedidoDTO;
import com.example.delivery.service.PedidoInputDTO;
import com.example.delivery.service.PedidoService;

//OCP: Esta é uma implementação concreta. O Controller depende da interface, não desta classe.
@Service
public class PedidoServiceImpl implements PedidoService {

	@Override
	public PedidoDTO criarPedido(PedidoInputDTO pedidoInputDTO) {
		// TODO Auto-generated method stub
		return null;
	}
// ... implementação
}