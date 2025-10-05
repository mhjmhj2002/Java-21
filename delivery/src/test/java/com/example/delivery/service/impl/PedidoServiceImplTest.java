package com.example.delivery.service.impl;

import com.example.delivery.dto.ItemPedidoInputDTO;
import com.example.delivery.dto.PedidoInputDTO;
import com.example.delivery.dto.PedidoResponseDTO;
import com.example.delivery.entity.Cliente;
import com.example.delivery.entity.Pedido;
import com.example.delivery.entity.Produto;
import com.example.delivery.entity.Restaurante;
import com.example.delivery.enums.StatusPedido;
import com.example.delivery.mapper.PedidoMapper;
import com.example.delivery.repository.ClienteRepository;
import com.example.delivery.repository.PedidoRepository;
import com.example.delivery.repository.ProdutoRepository;
import com.example.delivery.repository.RestauranteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para a classe PedidoServiceImpl. O foco é testar a lógica de
 * criação de pedidos, incluindo a busca por entidades relacionadas, o cálculo
 * do total e a persistência do pedido.
 */
@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

	// --- Mocks para todas as dependências do serviço ---
	@Mock
	private PedidoRepository pedidoRepository;
	@Mock
	private ClienteRepository clienteRepository;
	@Mock
	private RestauranteRepository restauranteRepository;
	@Mock
	private ProdutoRepository produtoRepository;
	@Mock
	private PedidoMapper pedidoMapper;

	// --- Instância real do serviço com mocks injetados ---
	@InjectMocks
	private PedidoServiceImpl pedidoService;

	// --- Dados de apoio para os testes ---
	private Cliente cliente;
	private Restaurante restaurante;
	private Produto produto1;
	private Produto produto2;
	private PedidoInputDTO pedidoInputDTO;
	private Pedido pedidoExistente;

	@BeforeEach
	void setUp() {
		// Configuração de entidades mockadas que serão retornadas pelos repositórios
		cliente = new Cliente();
		cliente.setId(1L);
		cliente.setNome("Cliente Teste");

		restaurante = new Restaurante();
		restaurante.setId(1L);

		produto1 = new Produto();
		produto1.setId(101L);
		produto1.setNome("Pizza Margherita");
		produto1.setPreco(new BigDecimal("45.00"));

		produto2 = new Produto();
		produto2.setId(102L);
		produto2.setNome("Refrigerante");
		produto2.setPreco(new BigDecimal("8.50"));

		// Configuração do DTO de entrada para o pedido
		ItemPedidoInputDTO item1 = new ItemPedidoInputDTO(101L, 1); // 1 Pizza
		ItemPedidoInputDTO item2 = new ItemPedidoInputDTO(102L, 2); // 2 Refrigerantes
		pedidoInputDTO = new PedidoInputDTO(1L, 1L, List.of(item1, item2));

		// Configura um pedido base para os testes de mudança de status
		pedidoExistente = new Pedido();
		pedidoExistente.setId(1L);
		pedidoExistente.setCliente(cliente);
		pedidoExistente.setRestaurante(restaurante);
		pedidoExistente.setStatus(StatusPedido.RECEBIDO); // Status inicial padrão
	}

	@Test
	@DisplayName("Deve criar um pedido com sucesso quando todos os dados são válidos")
	void deveCriarPedidoComSucesso() {
		// --- ARRANGE ---
		// Configura o comportamento dos mocks para o cenário de sucesso.
		// 1. Quando os repositórios buscarem por ID, eles devem encontrar as entidades.
		when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
		when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
		when(produtoRepository.findById(101L)).thenReturn(Optional.of(produto1));
		when(produtoRepository.findById(102L)).thenReturn(Optional.of(produto2));

		// 2. Quando o repositório de pedido salvar, ele deve retornar um pedido com ID.
		// Usamos um ArgumentCaptor para "capturar" o objeto Pedido que é passado para o
		// método save.
		ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
		// Simulamos que, após salvar, o pedido ganha um ID.
		when(pedidoRepository.save(pedidoCaptor.capture())).thenAnswer(invocation -> {
			Pedido pedidoSalvo = invocation.getArgument(0);
			pedidoSalvo.setId(99L); // Simula o ID gerado pelo banco
			return pedidoSalvo;
		});

		// 3. O mapper deve retornar um DTO de resposta mockado.
		when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(new PedidoResponseDTO(99L, 1L, "Cliente Teste",
				1L, "Restaurante Teste", null, StatusPedido.RECEBIDO, new BigDecimal("62.00"), LocalDateTime.now()));

		// --- ACT ---
		// Executa o método a ser testado
		PedidoResponseDTO resultado = pedidoService.criar(pedidoInputDTO);

		// --- ASSERT ---
		// 1. Verifica o DTO retornado
		assertNotNull(resultado);
		assertEquals(99L, resultado.id());
		assertEquals(new BigDecimal("62.00"), resultado.total());

		// 2. Usa o ArgumentCaptor para verificar o objeto que foi salvo
		Pedido pedidoSalvo = pedidoCaptor.getValue();
		assertNotNull(pedidoSalvo);
		assertEquals(StatusPedido.RECEBIDO, pedidoSalvo.getStatus()); // Verifica se o status inicial está correto
		assertEquals(2, pedidoSalvo.getItens().size()); // Verifica se os dois itens foram adicionados

		// Verifica o cálculo do total: (1 * 45.00) + (2 * 8.50) = 45.00 + 17.00 = 62.00
		// O compareTo é usado para comparar BigDecimals. 0 significa que são iguais.
		assertEquals(0, new BigDecimal("62.00").compareTo(pedidoSalvo.getTotal()));

		// 3. Verifica se os métodos dos mocks foram chamados
		verify(pedidoRepository, times(1)).save(any(Pedido.class));
		verify(clienteRepository, times(1)).findById(1L);
		verify(produtoRepository, times(1)).findById(101L);
		verify(produtoRepository, times(1)).findById(102L);
	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar criar pedido com produto inexistente")
	void deveLancarExcecaoAoCriarPedidoComProdutoInexistente() {
		// --- ARRANGE ---
		// Configura os mocks para um cenário onde um dos produtos não é encontrado.
		when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
		when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
		when(produtoRepository.findById(101L)).thenReturn(Optional.of(produto1));
		// O produto com ID 102L não será encontrado.
		when(produtoRepository.findById(102L)).thenReturn(Optional.empty());

		// --- ACT & ASSERT ---
		// Verifica se a exceção correta é lançada
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			pedidoService.criar(pedidoInputDTO);
		});

		// Verifica a mensagem de erro
		assertEquals("Produto não encontrado: 102", exception.getMessage());

		// Garante que o pedido NUNCA foi salvo, pois a transação deve ser revertida.
		verify(pedidoRepository, never()).save(any(Pedido.class));
	}

	@Test
	@DisplayName("Deve lançar exceção ao criar pedido com cliente inexistente")
	void deveLancarExcecaoAoCriarPedidoComClienteInexistente() {
		// --- ARRANGE ---
		// Cliente não será encontrado
		when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

		// --- ACT & ASSERT ---
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			pedidoService.criar(pedidoInputDTO);
		});

		assertEquals("Cliente não encontrado", exception.getMessage());
		verify(pedidoRepository, never()).save(any(Pedido.class));
	}

	@Test
	@DisplayName("Deve lançar exceção ao criar pedido com restaurante inexistente")
	void deveLancarExcecaoAoCriarPedidoComRestauranteInexistente() {
		// --- ARRANGE ---
		// Cliente é encontrado, mas o restaurante não
		when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
		when(restauranteRepository.findById(1L)).thenReturn(Optional.empty());

		// --- ACT & ASSERT ---
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			pedidoService.criar(pedidoInputDTO);
		});

		assertEquals("Restaurante não encontrado", exception.getMessage());
		verify(pedidoRepository, never()).save(any(Pedido.class));
	}

	@Test
	@DisplayName("Deve confirmar um pedido com sucesso")
	void deveConfirmarPedidoComSucesso() {
		// --- ARRANGE ---
		// O pedido começa com o status RECEBIDO
		pedidoExistente.setStatus(StatusPedido.RECEBIDO);
		when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));

		// Adicionamos lenient() para dizer ao Mockito para não ser estrito com este
		// stub.
		lenient().when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenAnswer(invocation -> {
			Pedido pedidoMapeado = invocation.getArgument(0);
			if (pedidoMapeado == null)
				return null; // Proteção contra null
			return new PedidoResponseDTO(pedidoMapeado.getId(), pedidoMapeado.getCliente().getId(),
					pedidoMapeado.getCliente().getNome(), pedidoMapeado.getRestaurante().getId(),
					pedidoMapeado.getRestaurante().getNome(), null, pedidoMapeado.getStatus(), pedidoMapeado.getTotal(),
					pedidoMapeado.getDataPedido());
		});

		// --- ACT ---
		pedidoService.confirmarPedido(1L);

		// --- ASSERT ---
		// Verifica se o status do objeto foi alterado para CONFIRMADO
		assertEquals(StatusPedido.CONFIRMADO, pedidoExistente.getStatus());
		// Verifica se o repositório foi chamado para salvar o estado alterado
		verify(pedidoRepository, times(1)).save(pedidoExistente);
	}

	@Test
	@DisplayName("Deve iniciar o preparo de um pedido com sucesso")
	void deveIniciarPreparoComSucesso() {
		// --- ARRANGE ---
		pedidoExistente.setStatus(StatusPedido.CONFIRMADO);
		when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));

		// Adicionamos lenient() para dizer ao Mockito para não ser estrito com este
		// stub.
		lenient().when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenAnswer(invocation -> {
			Pedido pedidoMapeado = invocation.getArgument(0);
			if (pedidoMapeado == null)
				return null; // Proteção contra null
			return new PedidoResponseDTO(pedidoMapeado.getId(), pedidoMapeado.getCliente().getId(),
					pedidoMapeado.getCliente().getNome(), pedidoMapeado.getRestaurante().getId(),
					pedidoMapeado.getRestaurante().getNome(), null, pedidoMapeado.getStatus(), pedidoMapeado.getTotal(),
					pedidoMapeado.getDataPedido());
		});

		// --- ACT ---
		pedidoService.iniciarPreparo(1L);

		// --- ASSERT ---
		assertEquals(StatusPedido.EM_PREPARO, pedidoExistente.getStatus());
		verify(pedidoRepository, times(1)).save(pedidoExistente);
	}

	@Test
	@DisplayName("Deve cancelar um pedido com sucesso")
	void deveCancelarPedidoComSucesso() {
		// --- ARRANGE ---
		pedidoExistente.setStatus(StatusPedido.RECEBIDO);
		when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));

		// Adicionamos lenient() para dizer ao Mockito para não ser estrito com este
		// stub.
		lenient().when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenAnswer(invocation -> {
			Pedido pedidoMapeado = invocation.getArgument(0);
			if (pedidoMapeado == null)
				return null; // Proteção contra null
			return new PedidoResponseDTO(pedidoMapeado.getId(), pedidoMapeado.getCliente().getId(),
					pedidoMapeado.getCliente().getNome(), pedidoMapeado.getRestaurante().getId(),
					pedidoMapeado.getRestaurante().getNome(), null, pedidoMapeado.getStatus(), pedidoMapeado.getTotal(),
					pedidoMapeado.getDataPedido());
		});

		// --- ACT ---
		pedidoService.cancelarPedido(1L);

		// --- ASSERT ---
		assertEquals(StatusPedido.CANCELADO, pedidoExistente.getStatus());
		verify(pedidoRepository, times(1)).save(pedidoExistente);
	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar confirmar um pedido que não existe")
	void deveLancarExcecaoAoConfirmarPedidoInexistente() {
		// --- ARRANGE ---
		when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

		// --- ACT & ASSERT ---
		assertThrows(EntityNotFoundException.class, () -> {
			pedidoService.confirmarPedido(99L);
		});
		// Garante que o método save nunca seja chamado
		verify(pedidoRepository, never()).save(any(Pedido.class));
	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar confirmar um pedido já cancelado")
	void deveLancarExcecaoAoConfirmarPedidoCancelado() {
		// --- ARRANGE ---
		// Configura o pedido com um status que não permite a transição para CONFIRMADO
		pedidoExistente.setStatus(StatusPedido.CANCELADO);
		when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));

		// --- ACT & ASSERT ---
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			pedidoService.confirmarPedido(1L);
		});

		assertEquals("O pedido não pode ser confirmado, pois seu status atual é: CANCELADO", exception.getMessage());
		verify(pedidoRepository, never()).save(any(Pedido.class));
	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar preparar um pedido com status incorreto")
	void deveLancarExcecaoAoPrepararPedidoComStatusIncorreto() {
		// --- ARRANGE ---
		// O pedido precisa estar CONFIRMADO para poder ir para EM_PREPARO.
		// Vamos testar com o status RECEBIDO.
		pedidoExistente.setStatus(StatusPedido.RECEBIDO);
		when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));

		// --- ACT & ASSERT ---
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			pedidoService.iniciarPreparo(1L);
		});

		assertEquals("O pedido não pode iniciar o preparo, pois seu status atual é: RECEBIDO", exception.getMessage());
		verify(pedidoRepository, never()).save(any(Pedido.class));
	}
	
	// ===================================================================
	// TESTES PARA despacharParaEntrega
	// ===================================================================

	@Test
	@DisplayName("Deve despachar um pedido para entrega com sucesso")
	void deveDespacharPedidoParaEntregaComSucesso() {
	    // --- ARRANGE ---
	    // Para despachar, o pedido deve estar EM_PREPARO
	    pedidoExistente.setStatus(StatusPedido.EM_PREPARO);
	    when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));

	    // Configuração do mapper como 'lenient' para evitar PotentialStubbingProblem
	    lenient().when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(mock(PedidoResponseDTO.class));

	    // --- ACT ---
	    pedidoService.despacharParaEntrega(1L);

	    // --- ASSERT ---
	    // Verifica se o status foi alterado corretamente
	    assertEquals(StatusPedido.SAIU_PARA_ENTREGA, pedidoExistente.getStatus());
	    // Verifica se o método save foi chamado para persistir a mudança
	    verify(pedidoRepository, times(1)).save(pedidoExistente);
	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar despachar pedido com status incorreto")
	void deveLancarExcecaoAoDespacharPedidoComStatusIncorreto() {
	    // --- ARRANGE ---
	    // O pedido está CONFIRMADO, mas deveria estar EM_PREPARO
	    pedidoExistente.setStatus(StatusPedido.CONFIRMADO);
	    when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));

	    // --- ACT & ASSERT ---
	    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
	        pedidoService.despacharParaEntrega(1L);
	    });

	    // Verifica a mensagem de erro exata
	    assertEquals("O pedido não pode sair para entrega, pois seu status atual é: CONFIRMADO", exception.getMessage());
	    // Garante que o pedido não foi salvo
	    verify(pedidoRepository, never()).save(any(Pedido.class));
	}


	// ===================================================================
	// TESTES PARA marcarComoEntregue
	// ===================================================================

	@Test
	@DisplayName("Deve marcar um pedido como entregue com sucesso")
	void deveMarcarPedidoComoEntregueComSucesso() {
	    // --- ARRANGE ---
	    // Para ser entregue, o pedido deve estar SAIU_PARA_ENTREGA
	    pedidoExistente.setStatus(StatusPedido.SAIU_PARA_ENTREGA);
	    when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));

	    // Configuração do mapper como 'lenient'
	    lenient().when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(mock(PedidoResponseDTO.class));

	    // --- ACT ---
	    pedidoService.marcarComoEntregue(1L);

	    // --- ASSERT ---
	    // Verifica se o status final é ENTREGUE
	    assertEquals(StatusPedido.ENTREGUE, pedidoExistente.getStatus());
	    // Verifica se o método save foi chamado
	    verify(pedidoRepository, times(1)).save(pedidoExistente);
	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar entregar pedido com status incorreto")
	void deveLancarExcecaoAoEntregarPedidoComStatusIncorreto() {
	    // --- ARRANGE ---
	    // O pedido está EM_PREPARO, mas deveria estar SAIU_PARA_ENTREGA
	    pedidoExistente.setStatus(StatusPedido.EM_PREPARO);
	    when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));

	    // --- ACT & ASSERT ---
	    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
	        pedidoService.marcarComoEntregue(1L);
	    });

	    // Verifica a mensagem de erro exata
	    assertEquals("O pedido não pode ser marcado como entregue, pois seu status atual é: EM_PREPARO", exception.getMessage());
	    // Garante que o pedido não foi salvo
	    verify(pedidoRepository, never()).save(any(Pedido.class));
	}


}
