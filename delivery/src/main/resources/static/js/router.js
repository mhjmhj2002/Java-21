import { loadComponent, renderPaginaRestaurantes, renderPaginaPedidos, renderPaginaAcompanhamento, renderPaginaClientes } from './ui.js';


// Mapeia o hash da URL para o arquivo da página correspondente
const routes = {
	'#restaurantes': {
		path: 'pages/restaurantes.html',
		onLoad: renderPaginaRestaurantes // Função a ser executada após carregar a página
	},
	'#clientes': {
		path: 'pages/clientes.html',
		onLoad: renderPaginaClientes
	},
	'#pedidos': { // NOVA ROTA
		path: 'pages/pedidos.html',
		onLoad: renderPaginaPedidos // Função que irá inicializar a página de pedidos
	},
	'#acompanhamento': {
		path: 'pages/acompanhamento.html',
		onLoad: renderPaginaAcompanhamento
	}
};

// Função principal para carregar a página
export async function loadPage() {
	// Pega o hash da URL ou define um padrão
	const hash = window.location.hash || '#restaurantes';
	const route = routes[hash];

	const appContent = document.getElementById('app-content');
	if (!route) {
		appContent.innerHTML = '<h1 class="text-danger">Página não encontrada</h1>';
		return;
	}

	// Carrega o HTML da página no container principal
	await loadComponent('app-content', route.path);

	// Executa a função específica daquela página (se existir)
	if (route.onLoad) {
		route.onLoad();
	}
}
