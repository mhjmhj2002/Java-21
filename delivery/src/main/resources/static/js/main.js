import { loadPage } from './router.js';
import { loadComponent } from './ui.js';

// Função de inicialização da aplicação
async function init() {
    // Carrega os componentes reutilizáveis (header e footer)
    await loadComponent('header-placeholder', 'components/header.html');
    await loadComponent('footer-placeholder', 'components/footer.html');

    // Adiciona um listener para o evento de mudança de hash na URL
    window.addEventListener('hashchange', loadPage);

    // Carrega a página inicial ou a página definida no hash
    loadPage();
}

// Inicia a aplicação quando o DOM estiver pronto
document.addEventListener('DOMContentLoaded', init);
