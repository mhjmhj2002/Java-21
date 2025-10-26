import pandas as pd
import random

# --- Configurações ---
NUMERO_DE_REGISTROS = 10000
NOME_ARQUIVO_SAIDA = "faixas_cep_lote.xlsx"

# --- Dados de Exemplo ---
OPERADORES = ["Correios", "FedEx", "Loggi", "Jadlog", "Total Express"]
TIPOS_ENTREGA = ["Normal", "Expresso"]
CIDADES_UF = {
    "SP": ["São Paulo", "Campinas", "Guarulhos", "Santos"],
    "RJ": ["Rio de Janeiro", "Niterói", "Duque de Caxias", "São Gonçalo"],
    "MG": ["Belo Horizonte", "Uberlândia", "Contagem", "Juiz de Fora"],
    "BA": ["Salvador", "Feira de Santana", "Vitória da Conquista"]
}

# --- Função Principal ---
def gerar_dados_lote(num_registros):
    """
    Gera uma lista de dicionários, cada um representando uma linha da planilha.
    """
    print(f"Gerando {num_registros} registros de faixas de CEP...")
    
    dados = []
    cep_atual = 10000000  # Começa de um CEP inicial válido (ex: 10000-000)
    tamanho_faixa = 500   # Cada faixa terá 500 CEPs

    for i in range(num_registros):
        # 1. Gerar a faixa de CEP sequencial e sem sobreposição
        cep_inicial = cep_atual
        cep_final = cep_atual + tamanho_faixa - 1
        
        # 2. Selecionar dados aleatórios para a linha
        uf_selecionada = random.choice(list(CIDADES_UF.keys()))
        cidade_selecionada = random.choice(CIDADES_UF[uf_selecionada])
        operador_selecionado = random.choice(OPERADORES)
        tipo_entrega_selecionado = random.choice(TIPOS_ENTREGA)

        # 3. Adicionar o registro à lista
        dados.append({
            'cepInicial': str(cep_inicial).zfill(8),
            'cepFinal': str(cep_final).zfill(8),
            'cidade': cidade_selecionada,
            'uf': uf_selecionada,
            'tipoEntrega': tipo_entrega_selecionado,
            'operadorLogisticoNome': operador_selecionado
        })
        
        # 4. Atualizar o CEP para a próxima iteração, garantindo que não haja sobreposição
        cep_atual = cep_final + 1

    print("Geração de dados concluída.")
    return dados

def salvar_em_excel(dados, nome_arquivo):
    """
    Converte os dados para um DataFrame do Pandas e salva como um arquivo .xlsx.
    """
    print(f"Criando DataFrame e salvando no arquivo '{nome_arquivo}'...")
    
    # Define a ordem correta das colunas
    colunas = [
        'cepInicial', 
        'cepFinal', 
        'cidade', 
        'uf', 
        'tipoEntrega', 
        'operadorLogisticoNome'
    ]
    
    df = pd.DataFrame(dados, columns=colunas)
    
    # Salva o DataFrame em um arquivo Excel, sem o índice da linha
    df.to_excel(nome_arquivo, index=False)
    
    print(f"Arquivo '{nome_arquivo}' criado com sucesso com {len(df)} linhas.")

# --- Execução do Script ---
if __name__ == "__main__":
    dados_gerados = gerar_dados_lote(NUMERO_DE_REGISTROS)
    salvar_em_excel(dados_gerados, NOME_ARQUIVO_SAIDA)

