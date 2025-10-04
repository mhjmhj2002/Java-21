document.addEventListener('DOMContentLoaded', () => {

    const API_BASE_URL = 'http://localhost:8080/api';

    // --- Elementos do DOM ---
    const restaurantesLista = document.getElementById('restaurantes-lista' );
    const loadingSpinner = document.getElementById('loading-spinner');
    const produtosSection = document.getElementById('produtos-section');
    const produtosLista = document.getElementById('produtos-lista');
    const nomeRestauranteSelecionado = document.getElementById('nome-restaurante-selecionado');

    // --- Elementos do Modal ---
    const restauranteModal = new bootstrap.Modal(document.getElementById('restaurante-modal'));
    const restauranteForm = document.getElementById('restaurante-form');
    const modalLabel = document.getElementById('modalLabel');
    const restauranteIdInput = document.getElementById('restaurante-id');
    const restauranteNomeInput = document.getElementById('restaurante-nome');
    const restauranteEnderecoInput = document.getElementById('restaurante-endereco');
    const btnNovoRestaurante = document.getElementById('btn-novo-restaurante');

    // ===================================================================
    // FUNÇÕES DE RESTAURANTE (CRUD)
    // ===================================================================

    /**
     * READ: Busca e exibe os restaurantes.
     */
    async function carregarRestaurantes() {
        loadingSpinner.style.display = 'block';
        restaurantesLista.innerHTML = '';
        try {
            const response = await fetch(`${API_BASE_URL}/restaurantes`);
            if (!response.ok) throw new Error(`Erro na requisição: ${response.statusText}`);
            
            const data = await response.json();
            const restaurantes = Array.isArray(data.content) ? data.content : data;

            if (!Array.isArray(restaurantes)) throw new Error("A resposta da API não é um array válido.");

            if (restaurantes.length === 0) {
                restaurantesLista.innerHTML = '<p class="text-muted">Nenhum restaurante cadastrado.</p>';
                return;
            }

            restaurantes.forEach(restaurante => {
                const cardHtml = `
                    <div class="col-md-6 col-lg-4 mb-4">
                        <div class="card h-100 shadow-sm">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-start">
                                    <div>
                                        <h5 class="card-title card-title-clickable" data-id="${restaurante.id}" data-nome="${restaurante.nome}">${restaurante.nome}</h5>
                                        <p class="card-text text-muted">${restaurante.endereco}</p>
                                    </div>
                                    <!-- Botões de Ação -->
                                    <div class="dropdown">
                                        <button class="btn btn-sm btn-light" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                            <i class="bi bi-three-dots-vertical"></i>
                                        </button>
                                        <ul class="dropdown-menu">
                                            <li><a class="dropdown-item btn-editar" href="#" data-id="${restaurante.id}"><i class="bi bi-pencil-fill text-primary"></i> Editar</a></li>
                                            <li><a class="dropdown-item btn-deletar" href="#" data-id="${restaurante.id}"><i class="bi bi-trash-fill text-danger"></i> Deletar</a></li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
                restaurantesLista.innerHTML += cardHtml;
            });

        } catch (error) {
            restaurantesLista.innerHTML = `<div class="alert alert-danger">Falha ao carregar os restaurantes.</div>`;
            console.error('Erro ao carregar restaurantes:', error);
        } finally {
            loadingSpinner.style.display = 'none';
        }
    }

    /**
     * CREATE / UPDATE: Salva um restaurante (novo ou existente).
     */
    async function salvarRestaurante(event) {
        event.preventDefault(); // Impede o recarregamento da página

        const id = restauranteIdInput.value;
        const nome = restauranteNomeInput.value;
        const endereco = restauranteEnderecoInput.value;

        const restauranteData = { nome, endereco };

        const isUpdating = id !== '';
        const url = isUpdating ? `${API_BASE_URL}/restaurantes/${id}` : `${API_BASE_URL}/restaurantes`;
        const method = isUpdating ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(restauranteData)
            });

            if (!response.ok) throw new Error('Falha ao salvar o restaurante.');

            restauranteModal.hide(); // Fecha o modal
            await carregarRestaurantes(); // Recarrega a lista para mostrar a alteração

        } catch (error) {
            console.error('Erro ao salvar restaurante:', error);
            alert('Não foi possível salvar o restaurante. Verifique o console.');
        }
    }

    /**
     * DELETE: Deleta um restaurante.
     */
    async function deletarRestaurante(id) {
        // Confirmação antes de deletar
        if (!confirm('Tem certeza que deseja deletar este restaurante?')) {
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/restaurantes/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) throw new Error('Falha ao deletar o restaurante.');

            await carregarRestaurantes(); // Recarrega a lista

        } catch (error) {
            console.error('Erro ao deletar restaurante:', error);
            alert('Não foi possível deletar o restaurante.');
        }
    }

    // ===================================================================
    // FUNÇÕES DE PRODUTO
    // ===================================================================
    
    async function carregarProdutos(restauranteId, nomeRestaurante) {
        // ... (código para carregar produtos, sem alterações)
    }

    // ===================================================================
    // LÓGICA DE EVENTOS
    // ===================================================================

    // Evento para abrir o modal em modo "Novo"
    btnNovoRestaurante.addEventListener('click', () => {
        restauranteForm.reset(); // Limpa o formulário
        restauranteIdInput.value = ''; // Garante que o ID está vazio
        modalLabel.textContent = 'Novo Restaurante';
        restauranteModal.show();
    });

    // Evento para salvar o formulário (Criação ou Edição)
    restauranteForm.addEventListener('submit', salvarRestaurante);

    // Delegação de eventos para os botões nos cards (Editar, Deletar, Ver Cardápio)
    restaurantesLista.addEventListener('click', async (event) => {
        const target = event.target;

        // Ação: Deletar
        if (target.classList.contains('btn-deletar') || target.closest('.btn-deletar')) {
            event.preventDefault();
            const id = target.dataset.id || target.closest('.btn-deletar').dataset.id;
            deletarRestaurante(id);
        }

        // Ação: Editar
        if (target.classList.contains('btn-editar') || target.closest('.btn-editar')) {
            event.preventDefault();
            const id = target.dataset.id || target.closest('.btn-editar').dataset.id;
            
            // Busca os dados atuais do restaurante para preencher o formulário
            try {
                const response = await fetch(`${API_BASE_URL}/restaurantes/${id}`);
                if (!response.ok) throw new Error('Restaurante não encontrado.');
                const restaurante = await response.json();

                // Preenche o formulário e abre o modal em modo "Edição"
                restauranteIdInput.value = restaurante.id;
                restauranteNomeInput.value = restaurante.nome;
                restauranteEnderecoInput.value = restaurante.endereco;
                modalLabel.textContent = 'Editar Restaurante';
                restauranteModal.show();

            } catch (error) {
                console.error('Erro ao buscar restaurante para edição:', error);
            }
        }

        // Ação: Ver Cardápio (clique no título)
        if (target.classList.contains('card-title-clickable')) {
            const id = target.dataset.id;
            const nome = target.dataset.nome;
            carregarProdutos(id, nome);
            produtosSection.scrollIntoView({ behavior: 'smooth' });
        }
    });

    // --- INICIALIZAÇÃO ---
    carregarRestaurantes();
});
