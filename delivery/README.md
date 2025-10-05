# 🚀 Delivery App - API RESTful e Frontend

![Java](https://img.shields.io/badge/Java-21-blue?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?style=for-the-badge&logo=spring)
![JPA/Hibernate](https://img.shields.io/badge/JPA%2FHibernate-orange?style=for-the-badge&logo=hibernate)
![H2 Database](https://img.shields.io/badge/H2%20Database-red?style=for-the-badge&logo=h2)
![JavaScript](https://img.shields.io/badge/JavaScript-ES6-yellow?style=for-the-badge&logo=javascript)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-purple?style=for-the-badge&logo=bootstrap)
![Maven](https://img.shields.io/badge/Maven-Build-blue?style=for-the-badge&logo=apachemaven)

Projeto full-stack desenvolvido para fins acadêmicos, simulando um sistema de delivery. A aplicação consiste em um backend robusto construído com **Java 21** e **Spring Boot 3**, e um frontend dinâmico e responsivo feito com **Vanilla JavaScript** e **Bootstrap 5**.

## 🎯 Objetivo

O objetivo principal é demonstrar a construção de uma aplicação web completa, desde a modelagem e implementação de uma API RESTful seguindo as melhores práticas (como os princípios **SOLID**), até o consumo dessa API por uma interface de usuário funcional e intuitiva.

## ✨ Funcionalidades

### Backend
-   **API RESTful Completa:** Endpoints para todas as operações CRUD (Create, Read, Update, Delete).
-   **Gerenciamento de Entidades:**
    -   Clientes
    -   Restaurantes
    -   Produtos (associados a restaurantes)
-   **Sistema de Pedidos:**
    -   Criação de pedidos complexos, associando cliente, restaurante e múltiplos itens.
    -   Gerenciamento do ciclo de vida dos pedidos com transições de status (Recebido, Confirmado, Em Preparo, etc.).
-   **Tratamento de Erros Centralizado:** Respostas de erro padronizadas para a API.
-   **Documentação Interativa:** API documentada com Swagger (Springdoc OpenAPI).
-   **Banco de Dados em Memória:** Utilização do H2 para facilitar o desenvolvimento e os testes.

### Frontend
-   **Single Page Application (SPA):** Arquitetura de página única com roteamento baseado em hash para uma navegação fluida.
-   **Interface Responsiva:** Layout adaptável a diferentes tamanhos de tela graças ao Bootstrap 5.
-   **Componentização:** Estrutura modular com componentes reutilizáveis (header, footer).
-   **CRUD Completo na UI:** Telas para cadastrar, visualizar, editar e deletar:
    -   Clientes
    -   Restaurantes
-   **Fluxo de Pedidos:**
    -   Tela para "Realizar Pedido" com um formulário dinâmico de múltiplos passos.
    -   Tela para "Acompanhar Pedidos", permitindo a visualização e atualização do status de cada pedido.

## 🛠️ Tecnologias Utilizadas

| Categoria | Tecnologia | Descrição |
| :--- | :--- | :--- |
| **Backend** | **Java 21** | Versão LTS mais recente da linguagem. |
| | **Spring Boot 3.2.5** | Framework principal para a criação da API. |
| | **Spring Data JPA** | Camada de persistência para acesso a dados. |
| | **Hibernate** | Implementação do ORM (Mapeamento Objeto-Relacional). |
| | **H2 Database** | Banco de dados em memória para desenvolvimento. |
| | **MapStruct** | Mapeamento automático entre Entidades e DTOs. |
| | **Lombok** | Redução de código boilerplate. |
| **Frontend** | **HTML5 / CSS3** | Estrutura e estilização das páginas. |
| | **Bootstrap 5** | Framework CSS para design responsivo e componentes de UI. |
| | **Vanilla JavaScript (ES6+)** | Lógica do frontend, incluindo consumo da API com `fetch`. |
| **Documentação** | **Springdoc OpenAPI** | Geração automática de documentação Swagger para a API. |
| **Build** | **Maven** | Gerenciamento de dependências e build do projeto. |

## 🏛️ Arquitetura e Princípios SOLID

A aplicação foi estruturada em camadas para garantir a separação de responsabilidades. O código-fonte do backend contém comentários específicos destacando a aplicação dos princípios **SOLID** para fins didáticos.

-   **(S) Single Responsibility Principle:** Classes com responsabilidades únicas (Controllers, Services, Repositories).
-   **(O) Open/Closed Principle:** Uso de interfaces na camada de serviço para permitir extensão sem modificação.
-   **(L) Liskov Substitution Principle:** Implementações de serviço que respeitam o contrato da interface.
-   **(I) Interface Segregation Principle:** Interfaces específicas para cada entidade.
-   **(D) Dependency Inversion Principle:** Injeção de Dependência do Spring para desacoplar os componentes.

## 🚀 Como Executar o Projeto

### Pré-requisitos
-   JDK 21 ou superior.
-   Apache Maven 3.8 ou superior.

### Passos para Execução

1.  **Clone o repositório:**
    ```bash
    git clone <url-do-seu-repositorio>
    cd nome-do-projeto
    ```

2.  **Compile e execute o backend com Maven:**
    O Maven irá compilar o projeto, baixar as dependências e iniciar o servidor web embutido.
    ```bash
    mvn spring-boot:run
    ```
    A aplicação estará rodando em `http://localhost:8080`.

3.  **Acesse a Aplicação Frontend:**
    Abra seu navegador e acesse a URL raiz. O Spring Boot servirá automaticamente o `index.html`.
    ➡️ **[http://localhost:8080/](http://localhost:8080/)**

4.  **Explore a Documentação da API (Swagger):**
    Para ver todos os endpoints do backend e interagir com eles diretamente.
    ➡️ **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

5.  **Acesse o Console do Banco H2:**
    Para visualizar as tabelas e os dados em tempo real.
    ➡️ **[http://localhost:8080/h2-console](http://localhost:8080/h2-console)**

    Use as seguintes credenciais para conectar:
    -   **JDBC URL:** `jdbc:h2:mem:deliverydb`
    -   **User Name:** `sa`
    -   **Password:** (deixe em branco)
