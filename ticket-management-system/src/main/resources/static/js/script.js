// JavaScript customizado para o sistema

// Funções para upload de arquivos
function initDropZone(dropZoneId, fileInputId) {
    const dropZone = document.getElementById(dropZoneId);
    const fileInput = document.getElementById(fileInputId);

    if (!dropZone || !fileInput) return;

    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        dropZone.addEventListener(eventName, preventDefaults, false);
    });

    function preventDefaults(e) {
        e.preventDefault();
        e.stopPropagation();
    }

    ['dragenter', 'dragover'].forEach(eventName => {
        dropZone.addEventListener(eventName, highlight, false);
    });

    ['dragleave', 'drop'].forEach(eventName => {
        dropZone.addEventListener(eventName, unhighlight, false);
    });

    function highlight() {
        dropZone.classList.add('bg-light');
    }

    function unhighlight() {
        dropZone.classList.remove('bg-light');
    }

    dropZone.addEventListener('drop', handleDrop, false);

    function handleDrop(e) {
        const dt = e.dataTransfer;
        const files = dt.files;
        fileInput.files = files;
        updateFileName(files[0]?.name);
    }

    fileInput.addEventListener('change', function() {
        updateFileName(this.files[0]?.name);
    });

    function updateFileName(fileName) {
        const fileNameElement = dropZone.querySelector('.file-name');
        if (fileNameElement) {
            fileNameElement.textContent = fileName || 'Nenhum arquivo selecionado';
        }
    }
}

// Inicializar drop zones quando a página carregar
document.addEventListener('DOMContentLoaded', function() {
    initDropZone('dropZone', 'fileInput');
});

// Funções para acompanhamento de processamento
function monitorarProcessamento(processId) {
    setInterval(() => {
        fetch(`/api/batch/status/${processId}`)
            .then(response => response.json())
            .then(data => {
                atualizarProgresso(data);
            })
            .catch(error => {
                console.error('Erro ao verificar status:', error);
            });
    }, 2000);
}

function atualizarProgresso(data) {
    const progressBar = document.getElementById('progressBar');
    const statusText = document.getElementById('statusText');
    const processed = document.getElementById('processedRecords');
    const total = document.getElementById('totalRecords');
    const success = document.getElementById('successCount');
    const errors = document.getElementById('errorCount');

    if (progressBar && statusText && processed && total && success && errors) {
        const progress = (data.processedRecords / data.totalRecords) * 100;
        progressBar.style.width = `${progress}%`;
        progressBar.textContent = `${Math.round(progress)}%`;
        
        statusText.textContent = data.status;
        processed.textContent = data.processedRecords;
        total.textContent = data.totalRecords;
        success.textContent = data.successCount;
        errors.textContent = data.errorCount;
    }
}

// Utilidades
function formatarData(dataString) {
    const data = new Date(dataString);
    return data.toLocaleString('pt-BR');
}

function mostrarNotificacao(mensagem, tipo = 'info') {
    // Implementar notificação toast
    console.log(`${tipo}: ${mensagem}`);
}