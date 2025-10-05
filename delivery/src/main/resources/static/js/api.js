const API_BASE_URL = 'http://localhost:8080/api';

// Função genérica para requisições
async function request(endpoint, options = {}) {
	const url = `${API_BASE_URL}${endpoint}`;
	const response = await fetch(url, options);
	if (!response.ok) {
		const errorData = await response.json().catch(() => ({ message: response.statusText }));
		throw new Error(errorData.message || 'Ocorreu um erro na requisição.');
	}
	// Retorna vazio para respostas 204 No Content (DELETE)
	return response.status === 204 ? null : response.json();
}

// Funções específicas para Restaurantes
export const api = {
	restaurantes: {
		getAll: () => request('/restaurantes?size=100'), // Traz todos para a tabela
		getById: (id) => request(`/restaurantes/${id}`),
		create: (data) => request('/restaurantes', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(data)
		}),
		update: (id, data) => request(`/restaurantes/${id}`, {
			method: 'PUT',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(data)
		}),
		delete: (id) => request(`/restaurantes/${id}`, { method: 'DELETE' }),
		getProdutos: (restauranteId) => request(`/restaurantes/${restauranteId}/produtos`)
	},
	clientes: {
		getAll: () => request('/clientes'),
		getById: (id) => request(`/clientes/${id}`),
		create: (data) => request('/clientes', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(data)
		}),
		update: (id, data) => request(`/clientes/${id}`, {
			method: 'PUT',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(data)
		}),
		delete: (id) => request(`/clientes/${id}`, { method: 'DELETE' })
	},
	pedidos: {
		create: (pedidoData) => request('/pedidos', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(pedidoData)
		}),
		getByCliente: (clienteId) => request(`/pedidos/cliente/${clienteId}`), // NOVO
		// Funções para mudança de status
		confirmar: (pedidoId) => request(`/pedidos/${pedidoId}/confirmar`, { method: 'PUT' }),
		preparar: (pedidoId) => request(`/pedidos/${pedidoId}/preparar`, { method: 'PUT' }),
		despachar: (pedidoId) => request(`/pedidos/${pedidoId}/despachar`, { method: 'PUT' }),
		entregar: (pedidoId) => request(`/pedidos/${pedidoId}/entregar`, { method: 'PUT' }),
		cancelar: (pedidoId) => request(`/pedidos/${pedidoId}/cancelar`, { method: 'PUT' })
	}
	// TODO: Adicionar objetos para clientes, produtos, etc.
	// clientes: { ... }
};
