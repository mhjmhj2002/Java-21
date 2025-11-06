# 🚚 Microserviço de Designação Logística (`designation-service`)

Este projeto é um microserviço Spring Boot responsável pela gestão e designação de faixas de CEP para operadores logísticos. Ele foi projetado para ser altamente escalável e resiliente, utilizando uma arquitetura orientada a eventos para processar grandes volumes de dados de forma assíncrona.

![Java](https://img.shields.io/badge/Java-21-blue.svg )
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg )
![JPA / Hibernate](https://img.shields.io/badge/JPA%20%2F%20Hibernate-6.x-orange.svg )
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Messaging-ff6600.svg )
![H2 Database](https://img.shields.io/badge/H2-In--Memory%20DB-lightgrey.svg )
![Maven](https://img.shields.io/badge/Maven-Build-red.svg )
![Lombok](https://img.shields.io/badge/Lombok-Code%20Gen-purple.svg )
![MapStruct](https://img.shields.io/badge/MapStruct-Mapping-yellow.svg )


## ✨ Funcionalidades Principais

1.  **CRUD de Faixas de CEP:**
    *   Endpoints REST para criar, ler, atualizar e deletar faixas de CEP.
    *   Validação de negócio para impedir a sobreposição de faixas de CEP, garantindo a integridade dos dados.

2.  **Designação de Pedidos:**
    *   Um endpoint que, dado um CEP, retorna o operador logístico responsável por aquela região.

3.  **Importação Assíncrona em Lote:**
    *   Um endpoint para upload de planilhas Excel (`.xlsx`) contendo dezenas de milhares de registros de faixas de CEP.
    *   O processamento é feito de forma assíncrona para não bloquear o usuário, utilizando uma arquitetura robusta com RabbitMQ.

4.  **Monitoramento de Progresso em Tempo Real:**
    *   Um endpoint que fornece o status e o progresso (percentual, sucessos, erros) de um lote de importação enquanto ele está sendo processado.

## 🏛️ Arquitetura

O sistema utiliza uma arquitetura de microserviços e é orientado a eventos, especialmente no processo de importação em lote.

### Fluxo de Importação em Lote

O fluxo de importação foi projetado para ser resiliente e escalável, lidando com centenas de milhares de registros sem travar a aplicação.

```ascii
+-------------+      1. Upload .xlsx      +---------------------+
|   Usuário   | ------------------------> |   LoteController    |
+-------------+                           +---------------------+
                                                     |
                                                     | 2. Chama LoteUploadService
                                                     v
+---------------------------------------------------------------------------------+
| LoteUploadService                                                               |
|   - Lê o arquivo Excel.                                                         |
|   - Cria uma entidade 'Lote' pai com status PENDENTE.                           |
|   - Quebra os 200k registros em 400 sub-lotes de 500.                           |
|   - Salva todos os 200k 'ItemLote' no banco (em batch).                         |
|   - Envia 400 mensagens para a fila, uma para cada sub-lote.                    |
+---------------------------------------------------------------------------------+
                                                     |
                                                     | 3. Envia 400 mensagens
                                                     v
+---------------------------------------------------------------------------------+
| RabbitMQ (Fila: lote.faixas.v1)                                                 |
+---------------------------------------------------------------------------------+
       | | | |
       | | | | 4. Workers consomem as mensagens em paralelo
       v v v v
+---------------------------------------------------------------------------------+
| LoteFaixaCepListener (Múltiplos Workers)                                        |
|   - Cada worker recebe uma mensagem com um 'loteId' de sub-lote.                |
|   - Chama LoteProcessingService.processarSubLote(loteId).                       |
+---------------------------------------------------------------------------------+
                                                     |
                                                     | 5. Processa e reporta
                                                     v
+---------------------------------------------------------------------------------+
| LoteProgressAggregator (Singleton em Memória)                                   |
|   - Recebe o resultado de cada sub-lote (sucessos/erros).                       |
|   - Consolida o progresso em um mapa em memória.                                |
|   - Quando o último sub-lote é processado, salva o estado final no banco.      |
+---------------------------------------------------------------------------------+
```

## 🚀 Como Executar

### Pré-requisitos

1.  **Java 21+**
2.  **Apache Maven 3.8+**
3.  **Docker e Docker Compose** (para rodar o RabbitMQ)

### 1. Iniciar o RabbitMQ

Este serviço depende de uma instância do RabbitMQ. A forma mais fácil de iniciá-la é via Docker.

```bash
# Baixa e inicia um container do RabbitMQ com a interface de gerenciamento
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.13-management
```

*   A aplicação se conectará na porta `5672`.
*   Você pode acessar a interface de gerenciamento em **[http://localhost:15672](http://localhost:15672)** (login: `guest` / `guest`).

### 2. Configurar a Aplicação

O arquivo `src/main/resources/application.properties` contém as configurações principais. As configurações padrão para RabbitMQ e H2 Database já estão prontas para o ambiente de desenvolvimento.

```properties
# Configuração do Servidor
server.port=8082

# Configuração do Banco de Dados em Memória H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.datasource.url=jdbc:h2:mem:designationdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Configuração do JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Configuração do RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# Configuração de Batch Insert do Hibernate
spring.jpa.properties.hibernate.jdbc.batch_size=100
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Configuração de Upload de Arquivo
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=20MB
```

### 3. Compilar e Rodar

Use o Maven para compilar e executar a aplicação:

```bash
# Compila o projeto e gera o .jar
mvn clean install

# Executa a aplicação
java -jar target/designation-service-0.0.1-SNAPSHOT.jar
```

Alternativamente, você pode rodar diretamente via Maven:

```bash
mvn spring-boot:run
```

A aplicação estará disponível na porta `8082`.

## 📖 Documentação da API

A API está documentada utilizando **SpringDoc (OpenAPI 3)**. Após iniciar a aplicação, acesse a interface do Swagger para visualizar e interagir com todos os endpoints disponíveis:

*   **[http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)**

## 🎓 Objetivos Acadêmicos

Este projeto foi desenvolvido com um forte foco educacional, aplicando os cinco princípios **SOLID** no design do backend. Comentários no código-fonte (`// SOLID: ...`) destacam onde cada princípio foi aplicado para facilitar o estudo e a compreensão. Além disso, a arquitetura de processamento em lote demonstra padrões avançados para sistemas distribuídos e concorrentes.