import { api } from './api.js';

// Função para carregar um componente HTML em um elemento
export async function loadComponent(elementId, path) {
	const element = document.getElementById(elementId);
	if (element) {
		const response = await fetch(path);
		element.innerHTML = await response.text();
	}
}

// Função principal para renderizar a página de restaurantes
export async function renderRestaurantes() {
	const tabelaBody = document.getElementById('tabela-restaurantes');
	const loadingSpinner = document.getElementById('loading-spinner');
	if (!tabelaBody || !loadingSpinner) return;

	loadingSpinner.style.display = 'block';
	tabelaBody.innerHTML = '';

	try {
		const page = await api.restaurantes.getAll();
		const restaurantes = Array.isArray(page.content) ? page.content : page;

		if (restaurantes.length === 0) {
			tabelaBody.innerHTML = '<tr><td colspan="4" class="text-center">Nenhum restaurante cadastrado.</td></tr>';
			return;
		}

		restaurantes.forEach(restaurante => {
			const row = `
                <tr>
                    <td>${restaurante.id}</td>
                    <td>${restaurante.nome}</td>
                    <td>${restaurante.endereco}</td>
                    <td class="text-end">
                        <button class="btn btn-sm btn-outline-primary btn-editar" data-id="${restaurante.id}">Editar</button>
                        <button class="btn btn-sm btn-outline-danger btn-deletar" data-id="${restaurante.id}">Deletar</button>
                    </td>
                </tr>
            `;
			tabelaBody.innerHTML += row;
		});
	} catch (error) {
		tabelaBody.innerHTML = `<tr><td colspan="4" class="text-center text-danger">Erro ao carregar dados: ${error.message}</td></tr>`;
	} finally {
		loadingSpinner.style.display = 'none';
	}

	// TODO: Adicionar lógica para os botões de novo, editar e deletar.
}

// ===================================================================
// LÓGICA DA PÁGINA DE PEDIDOS
// ===================================================================

// Variável para manter o estado do carrinho
let carrinho = [];

export async function renderPaginaPedidos() {
	// 1. Referenciar todos os elementos da página
	const selectCliente = document.getElementById('select-cliente');
	const selectRestaurante = document.getElementById('select-restaurante');
	const cardapioSection = document.getElementById('cardapio-section');
	const cardapioLista = document.getElementById('cardapio-lista');
	const resumoSection = document.getElementById('resumo-section');
	const carrinhoLista = document.getElementById('carrinho-lista');
	const totalPedidoEl = document.getElementById('total-pedido');
	const btnFinalizarPedido = document.getElementById('btn-finalizar-pedido');

	// Reseta o carrinho ao carregar a página
	carrinho = [];

	// 2. Carregar os selects de Cliente e Restaurante
	async function carregarSelects() {
		try {
			// 1. Faz as duas chamadas à API. Ambas retornam um array simples.
			const [clientes, restaurantes] = await Promise.all([
				api.clientes.getAll(),
				api.restaurantes.getAll()
			]);

			// 2. Validação para garantir que ambas as respostas são arrays
			if (!Array.isArray(clientes) || !Array.isArray(restaurantes)) {
				throw new Error("Formato de dados inválido recebido da API. Esperava-se um array.");
			}

			// 3. Preenche o select de clientes
			const selectCliente = document.getElementById('select-cliente');
			selectCliente.innerHTML = '<option selected disabled value="">Selecione...</option>';
			clientes.forEach(c => {
				selectCliente.innerHTML += `<option value="${c.id}">${c.nome}</option>`;
			});

			// 4. Preenche o select de restaurantes
			const selectRestaurante = document.getElementById('select-restaurante');
			selectRestaurante.innerHTML = '<option selected disabled value="">Selecione...</option>';
			restaurantes.forEach(r => {
				selectRestaurante.innerHTML += `<option value="${r.id}">${r.nome}</option>`;
			});

		} catch (error) {
			console.error("Erro ao carregar dados iniciais do pedido:", error);
			alert("Não foi possível carregar os dados para criar o pedido. Verifique o console.");
		}
	}

	// 3. Carregar o cardápio quando um restaurante for selecionado
	selectRestaurante.addEventListener('change', async () => {
		const restauranteId = selectRestaurante.value;
		if (!restauranteId) return;

		cardapioSection.classList.remove('d-none');
		cardapioLista.innerHTML = '<div class="text-center"><div class="spinner-border spinner-border-sm"></div></div>';
		carrinho = []; // Limpa o carrinho se mudar de restaurante
		atualizarResumoPedido();

		try {
			const produtos = await api.restaurantes.getProdutos(restauranteId);
			cardapioLista.innerHTML = '';
			produtos.forEach(produto => {
				cardapioLista.innerHTML += `
                    <div class="list-group-item d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="mb-1">${produto.nome}</h6>
                            <small class="text-muted">R$ ${produto.preco.toFixed(2)}</small>
                        </div>
                        <button class="btn btn-sm btn-outline-primary btn-add-carrinho" data-produto-id="${produto.id}" data-nome="${produto.nome}" data-preco="${produto.preco}">
                            Adicionar
                        </button>
                    </div>
                `;
			});
		} catch (error) {
			cardapioLista.innerHTML = '<div class="list-group-item text-danger">Erro ao carregar cardápio.</div>';
		}
	});

	// 4. Adicionar item ao carrinho
	cardapioLista.addEventListener('click', (e) => {
		if (e.target.classList.contains('btn-add-carrinho')) {
			const produtoId = Number(e.target.dataset.produtoId);
			const nome = e.target.dataset.nome;
			const preco = Number(e.target.dataset.preco);

			// Verifica se o item já está no carrinho
			const itemExistente = carrinho.find(item => item.produtoId === produtoId);
			if (itemExistente) {
				itemExistente.quantidade++;
			} else {
				carrinho.push({ produtoId, nome, preco, quantidade: 1 });
			}
			atualizarResumoPedido();
		}
	});

	// 5. Atualizar a UI do resumo e total
	function atualizarResumoPedido() {
		if (carrinho.length > 0) {
			resumoSection.classList.remove('d-none');
		} else {
			resumoSection.classList.add('d-none');
		}

		carrinhoLista.innerHTML = '';
		let total = 0;

		carrinho.forEach(item => {
			total += item.preco * item.quantidade;
			carrinhoLista.innerHTML += `
                <li class="list-group-item d-flex justify-content-between align-items-center">
                    ${item.nome} (x${item.quantidade})
                    <span>R$ ${(item.preco * item.quantidade).toFixed(2)}</span>
                </li>
            `;
		});

		totalPedidoEl.textContent = `R$ ${total.toFixed(2)}`;
		// Habilita o botão de finalizar apenas se tudo estiver preenchido
		btnFinalizarPedido.disabled = !(selectCliente.value && selectRestaurante.value && carrinho.length > 0);
	}

	// 6. Finalizar o pedido
	btnFinalizarPedido.addEventListener('click', async () => {
		const pedidoData = {
			clienteId: Number(selectCliente.value),
			restauranteId: Number(selectRestaurante.value),
			itens: carrinho.map(item => ({
				produtoId: item.produtoId,
				quantidade: item.quantidade
			}))
		};

		try {
			btnFinalizarPedido.disabled = true;
			btnFinalizarPedido.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Finalizando...';

			await api.pedidos.create(pedidoData);

			alert('Pedido realizado com sucesso!');
			// Redireciona para outra página ou limpa o formulário
			window.location.hash = '#restaurantes';

		} catch (error) {
			console.error("Erro ao finalizar pedido:", error);
			alert(`Falha ao realizar o pedido: ${error.message}`);
		} finally {
			btnFinalizarPedido.disabled = false;
			btnFinalizarPedido.innerHTML = '<i class="bi bi-check-circle-fill"></i> Finalizar Pedido';
		}
	});

	// Chama a função para carregar os dados iniciais
	await carregarSelects();
}

// ===================================================================
// LÓGICA DA PÁGINA DE ACOMPANHAMENTO
// ===================================================================

export async function renderPaginaAcompanhamento() {
	const selectCliente = document.getElementById('select-cliente-acompanhamento');
	const pedidosContainer = document.getElementById('pedidos-container');
	const pedidosPlaceholder = document.getElementById('pedidos-placeholder');

	// 1. Carregar o select de clientes
	try {
		const clientes = await api.clientes.getAll();
		selectCliente.innerHTML = '<option selected disabled value="">Selecione...</option>';
		clientes.forEach(c => {
			selectCliente.innerHTML += `<option value="${c.id}">${c.nome}</option>`;
		});
	} catch (error) {
		pedidosPlaceholder.innerHTML = '<p class="text-danger">Erro ao carregar clientes.</p>';
	}

	// 2. Listener para quando um cliente for selecionado
	selectCliente.addEventListener('change', async () => {
		const clienteId = selectCliente.value;
		if (!clienteId) return;

		pedidosContainer.innerHTML = '';
		pedidosPlaceholder.innerHTML = '<div class="spinner-border spinner-border-sm"></div> Carregando pedidos...';
		pedidosPlaceholder.style.display = 'block';

		try {
			const pedidos = await api.pedidos.getByCliente(clienteId);
			pedidosPlaceholder.style.display = 'none';

			if (pedidos.length === 0) {
				pedidosPlaceholder.innerHTML = '<p>Nenhum pedido encontrado para este cliente.</p>';
				pedidosPlaceholder.style.display = 'block';
				return;
			}

			// Ordena os pedidos do mais recente para o mais antigo
			pedidos.sort((a, b) => new Date(b.dataPedido) - new Date(a.dataPedido));

			pedidos.forEach(pedido => {
				pedidosContainer.innerHTML += criarCardPedido(pedido);
			});

		} catch (error) {
			pedidosPlaceholder.innerHTML = `<p class="text-danger">Erro ao carregar pedidos: ${error.message}</p>`;
			pedidosPlaceholder.style.display = 'block';
		}
	});

	// 3. Delegação de eventos para os botões de ação nos cards
	pedidosContainer.addEventListener('click', async (e) => {
		const button = e.target.closest('.btn-acao-pedido');
		if (!button) return;

		const pedidoId = button.dataset.pedidoId;
		const acao = button.dataset.acao;

		if (!confirm(`Tem certeza que deseja "${acao}" o pedido #${pedidoId}?`)) return;

		try {
			// Chama a função da API correspondente à ação
			await api.pedidos[acao](pedidoId);
			// Recarrega a lista de pedidos do cliente selecionado para refletir a mudança
			selectCliente.dispatchEvent(new Event('change'));
			alert(`Pedido #${pedidoId} atualizado com sucesso!`);
		} catch (error) {
			alert(`Erro ao atualizar o pedido: ${error.message}`);
		}
	});
}

/**
 * Cria o HTML para um card de pedido.
 * @param {object} pedido - O objeto do pedido.
 * @returns {string} O HTML do card.
 */
function criarCardPedido(pedido) {
	const statusInfo = getStatusInfo(pedido.status);
	const acoesHtml = getAcoesDisponiveis(pedido.status, pedido.id);

	return `
        <div class="col-md-6 col-lg-4">
            <div class="card shadow-sm">
                <div class="card-header d-flex justify-content-between">
                    <strong>Pedido #${pedido.id}</strong>
                    <span class="badge ${statusInfo.cor}">${statusInfo.texto}</span>
                </div>
                <div class="card-body">
                    <p><strong>Cliente:</strong> ${pedido.nomeCliente}</p>
                    <p><strong>Restaurante:</strong> ${pedido.nomeRestaurante}</p>
                    <p><strong>Total:</strong> R$ ${pedido.total.toFixed(2)}</p>
                    <p><strong>Data:</strong> ${new Date(pedido.dataPedido).toLocaleString('pt-BR')}</p>
                    <div class="mt-3">
                        <h6>Ações:</h6>
                        ${acoesHtml}
                    </div>
                </div>
            </div>
        </div>
    `;
}

/**
 * Retorna a cor e o texto para um determinado status.
 */
function getStatusInfo(status) {
	const statusMap = {
		RECEBIDO: { texto: 'Recebido', cor: 'bg-secondary' },
		CONFIRMADO: { texto: 'Confirmado', cor: 'bg-info' },
		EM_PREPARO: { texto: 'Em Preparo', cor: 'bg-warning text-dark' },
		SAIU_PARA_ENTREGA: { texto: 'Em Rota', cor: 'bg-primary' },
		ENTREGUE: { texto: 'Entregue', cor: 'bg-success' },
		CANCELADO: { texto: 'Cancelado', cor: 'bg-danger' }
	};
	return statusMap[status] || { texto: status, cor: 'bg-light text-dark' };
}

/**
 * Retorna os botões de ação disponíveis com base no status atual do pedido.
 */
function getAcoesDisponiveis(status, pedidoId) {
	let botoes = '';
	const podeCancelar = status !== 'ENTREGUE' && status !== 'CANCELADO';

	switch (status) {
		case 'RECEBIDO':
			botoes += `<button class="btn btn-sm btn-primary me-2 btn-acao-pedido" data-pedido-id="${pedidoId}" data-acao="confirmar">Confirmar</button>`;
			break;
		case 'CONFIRMADO':
			botoes += `<button class="btn btn-sm btn-primary me-2 btn-acao-pedido" data-pedido-id="${pedidoId}" data-acao="preparar">Preparar</button>`;
			break;
		case 'EM_PREPARO':
			botoes += `<button class="btn btn-sm btn-primary me-2 btn-acao-pedido" data-pedido-id="${pedidoId}" data-acao="despachar">Despachar</button>`;
			break;
		case 'SAIU_PARA_ENTREGA':
			botoes += `<button class="btn btn-sm btn-primary me-2 btn-acao-pedido" data-pedido-id="${pedidoId}" data-acao="entregar">Entregar</button>`;
			break;
	}

	if (podeCancelar) {
		botoes += `<button class="btn btn-sm btn-outline-danger btn-acao-pedido" data-pedido-id="${pedidoId}" data-acao="cancelar">Cancelar</button>`;
	}

	return botoes || '<p class="text-muted small">Nenhuma ação disponível.</p>';
}

// ===================================================================
// LÓGICA DA PÁGINA DE CLIENTES (CRUD)
// ===================================================================

export async function renderPaginaClientes() {
    // --- Referências aos elementos do DOM ---
    const tabelaBody = document.getElementById('tabela-clientes');
    const loadingSpinner = document.getElementById('loading-spinner');
    const btnNovoCliente = document.getElementById('btn-novo-cliente');
    
    // --- Referências ao Modal ---
    const clienteModal = new bootstrap.Modal(document.getElementById('cliente-modal'));
    const clienteForm = document.getElementById('cliente-form');
    const clienteModalLabel = document.getElementById('clienteModalLabel');
    const clienteIdInput = document.getElementById('cliente-id');
    const clienteNomeInput = document.getElementById('cliente-nome');
    const clienteEmailInput = document.getElementById('cliente-email');
    const clienteTelefoneInput = document.getElementById('cliente-telefone');

    // --- Função para carregar e renderizar a tabela ---
    async function carregarTabela() {
        loadingSpinner.style.display = 'block';
        tabelaBody.innerHTML = '';
        try {
            const clientes = await api.clientes.getAll();
            if (clientes.length === 0) {
                tabelaBody.innerHTML = '<tr><td colspan="5" class="text-center">Nenhum cliente cadastrado.</td></tr>';
                return;
            }
            clientes.forEach(cliente => {
                const row = `
                    <tr>
                        <td>${cliente.id}</td>
                        <td>${cliente.nome}</td>
                        <td>${cliente.email}</td>
                        <td>${cliente.telefone || 'N/A'}</td>
                        <td class="text-end">
                            <button class="btn btn-sm btn-outline-primary btn-editar-cliente" data-id="${cliente.id}"><i class="bi bi-pencil"></i></button>
                            <button class="btn btn-sm btn-outline-danger btn-deletar-cliente" data-id="${cliente.id}"><i class="bi bi-trash"></i></button>
                        </td>
                    </tr>
                `;
                tabelaBody.innerHTML += row;
            });
        } catch (error) {
            tabelaBody.innerHTML = `<tr><td colspan="5" class="text-center text-danger">Erro ao carregar clientes: ${error.message}</td></tr>`;
        } finally {
            loadingSpinner.style.display = 'none';
        }
    }

    // --- Função para salvar (Criar ou Atualizar) ---
    async function salvarCliente(event) {
        event.preventDefault();
        const id = clienteIdInput.value;
        const clienteData = {
            nome: clienteNomeInput.value,
            email: clienteEmailInput.value,
            telefone: clienteTelefoneInput.value
        };

        try {
            if (id) { // Atualizar (PUT)
                await api.clientes.update(id, clienteData);
            } else { // Criar (POST)
                await api.clientes.create(clienteData);
            }
            clienteModal.hide();
            await carregarTabela();
        } catch (error) {
            alert(`Erro ao salvar cliente: ${error.message}`);
        }
    }

    // --- Lógica de Eventos ---

    // Abrir modal para novo cliente
    btnNovoCliente.addEventListener('click', () => {
        clienteForm.reset();
        clienteIdInput.value = '';
        clienteModalLabel.textContent = 'Novo Cliente';
        clienteModal.show();
    });

    // Salvar formulário
    clienteForm.addEventListener('submit', salvarCliente);

    // Delegação de eventos para botões de editar e deletar
    tabelaBody.addEventListener('click', async (e) => {
        const target = e.target.closest('button');
        if (!target) return;

        const id = target.dataset.id;

        // Ação de Deletar
        if (target.classList.contains('btn-deletar-cliente')) {
            if (confirm(`Tem certeza que deseja deletar o cliente #${id}?`)) {
                try {
                    await api.clientes.delete(id);
                    await carregarTabela();
                } catch (error) {
                    alert(`Erro ao deletar cliente: ${error.message}`);
                }
            }
        }

        // Ação de Editar
        if (target.classList.contains('btn-editar-cliente')) {
            try {
                const cliente = await api.clientes.getById(id);
                clienteIdInput.value = cliente.id;
                clienteNomeInput.value = cliente.nome;
                clienteEmailInput.value = cliente.email;
                clienteTelefoneInput.value = cliente.telefone;
                clienteModalLabel.textContent = 'Editar Cliente';
                clienteModal.show();
            } catch (error) {
                alert(`Erro ao buscar dados do cliente: ${error.message}`);
            }
        }
    });

    // --- Carregamento Inicial ---
    await carregarTabela();
}

// ===================================================================
// LÓGICA DA PÁGINA DE RESTAURANTES (CRUD)
// ===================================================================

export async function renderPaginaRestaurantes() {
    // --- Referências aos elementos do DOM ---
    const tabelaBody = document.getElementById('tabela-restaurantes');
    const loadingSpinner = document.getElementById('loading-spinner');
    const btnNovoRestaurante = document.getElementById('btn-novo-restaurante');
    
    // --- Referências ao Modal ---
    const restauranteModal = new bootstrap.Modal(document.getElementById('restaurante-modal'));
    const restauranteForm = document.getElementById('restaurante-form');
    const restauranteModalLabel = document.getElementById('restauranteModalLabel');
    const restauranteIdInput = document.getElementById('restaurante-id');
    const restauranteNomeInput = document.getElementById('restaurante-nome');
    const restauranteEnderecoInput = document.getElementById('restaurante-endereco');

    // --- Função para carregar e renderizar a tabela ---
    async function carregarTabela() {
        loadingSpinner.style.display = 'block';
        tabelaBody.innerHTML = '';
        try {
            // A API retorna um array simples, conforme seu controller
            const restaurantes = await api.restaurantes.getAll();
            if (restaurantes.length === 0) {
                tabelaBody.innerHTML = '<tr><td colspan="4" class="text-center">Nenhum restaurante cadastrado.</td></tr>';
                return;
            }
            restaurantes.forEach(restaurante => {
                const row = `
                    <tr>
                        <td>${restaurante.id}</td>
                        <td>${restaurante.nome}</td>
                        <td>${restaurante.endereco}</td>
                        <td class="text-end">
                            <button class="btn btn-sm btn-outline-primary btn-editar-restaurante" data-id="${restaurante.id}"><i class="bi bi-pencil"></i></button>
                            <button class="btn btn-sm btn-outline-danger btn-deletar-restaurante" data-id="${restaurante.id}"><i class="bi bi-trash"></i></button>
                        </td>
                    </tr>
                `;
                tabelaBody.innerHTML += row;
            });
        } catch (error) {
            tabelaBody.innerHTML = `<tr><td colspan="4" class="text-center text-danger">Erro ao carregar restaurantes: ${error.message}</td></tr>`;
        } finally {
            loadingSpinner.style.display = 'none';
        }
    }

    // --- Função para salvar (Criar ou Atualizar) ---
    async function salvarRestaurante(event) {
        event.preventDefault();
        const id = restauranteIdInput.value;
        const restauranteData = {
            nome: restauranteNomeInput.value,
            endereco: restauranteEnderecoInput.value
        };

        // Para o método POST, o DTO pode precisar do ID nulo ou ausente
        if (id) {
            restauranteData.id = id;
        }

        try {
            if (id) { // Atualizar (PUT)
                await api.restaurantes.update(id, restauranteData);
            } else { // Criar (POST)
                await api.restaurantes.create(restauranteData);
            }
            restauranteModal.hide();
            await carregarTabela();
        } catch (error) {
            alert(`Erro ao salvar restaurante: ${error.message}`);
        }
    }

    // --- Lógica de Eventos ---

    // Abrir modal para novo restaurante
    btnNovoRestaurante.addEventListener('click', () => {
        restauranteForm.reset();
        restauranteIdInput.value = '';
        restauranteModalLabel.textContent = 'Novo Restaurante';
        restauranteModal.show();
    });

    // Salvar formulário
    restauranteForm.addEventListener('submit', salvarRestaurante);

    // Delegação de eventos para botões de editar e deletar
    tabelaBody.addEventListener('click', async (e) => {
        const target = e.target.closest('button');
        if (!target) return;

        const id = target.dataset.id;

        // Ação de Deletar
        if (target.classList.contains('btn-deletar-restaurante')) {
            if (confirm(`Tem certeza que deseja deletar o restaurante #${id}?`)) {
                try {
                    await api.restaurantes.delete(id);
                    await carregarTabela();
                } catch (error) {
                    alert(`Erro ao deletar restaurante: ${error.message}`);
                }
            }
        }

        // Ação de Editar
        if (target.classList.contains('btn-editar-restaurante')) {
            try {
                const restaurante = await api.restaurantes.getById(id);
                restauranteIdInput.value = restaurante.id;
                restauranteNomeInput.value = restaurante.nome;
                restauranteEnderecoInput.value = restaurante.endereco;
                restauranteModalLabel.textContent = 'Editar Restaurante';
                restauranteModal.show();
            } catch (error) {
                alert(`Erro ao buscar dados do restaurante: ${error.message}`);
            }
        }
    });

    // --- Carregamento Inicial ---
    await carregarTabela();
}