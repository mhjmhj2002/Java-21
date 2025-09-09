# Ticket Management System

Sistema de gerenciamento de tickets com processamento em lote via arquivos Excel.

## 📋 Descrição

Sistema desenvolvido em Spring Boot para gerenciamento de tickets e clientes, com funcionalidade de processamento em lote através de upload de arquivos Excel.

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.5.5**
- **Spring Data JPA**
- **Thymeleaf** (Templates)
- **H2 Database** (Desenvolvimento)
- **MySQL** (Produção)
- **Apache POI** (Processamento Excel)
- **Springdoc OpenAPI** (Documentação Swagger)
- **WebSocket** (Comunicação em tempo real)
- **Lombok** (Redução de boilerplate code)

## 📦 Dependências Principais

```xml
<!-- Spring Boot Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Thymeleaf -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<!-- Apache POI para Excel -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.4</version>
</dependency>

<!-- Swagger/OpenAPI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>

<!-- WebSocket -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

## 🏗️ Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── com/example/ticketsystem/
│   │       ├── controller/     # Controladores REST e MVC
│   │       ├── entity/         # Entidades JPA
│   │       ├── repository/     # Repositórios Spring Data
│   │       ├── service/        # Lógica de negócio
│   │       └── dto/           # Data Transfer Objects
│   └── resources/
│       ├── templates/          # Templates Thymeleaf
│       ├── static/             # Arquivos estáticos
│       └── application.properties
```

## 🌐 Endpoints da API

### 🎫 Gestão de Tickets
- `GET /tickets` - Lista todos os tickets
- `GET /tickets/novo` - Formulário de novo ticket
- `POST /tickets` - Cria um novo ticket
- `GET /tickets/editar/{id}` - Formulário de edição
- `POST /tickets/editar/{id}` - Atualiza um ticket
- `GET /tickets/excluir/{id}` - Exclui um ticket

### 👥 Gestão de Clientes
- `GET /clientes` - Lista todos os clientes
- `GET /clientes/novo` - Formulário de novo cliente
- `POST /clientes` - Cria um novo cliente
- `GET /clientes/editar/{id}` - Formulário de edição
- `POST /clientes/editar/{id}` - Atualiza um cliente
- `GET /clientes/excluir/{id}` - Exclui um cliente

### 📤 Processamento em Lote (Batch)
- `GET /batch/upload` - Formulário de upload
- `POST /batch/upload` - Upload de arquivo Excel
- `GET /batch/status` - Status dos processos
- `GET /batch/status/{id}` - Status específico
- `GET /batch/all` - Todos os processos
- `GET /batch/recent` - Processos recentes
- `POST /batch/cancel/{id}` - Cancela processo
- `GET /batch/stats` - Estatísticas

## 📊 Documentação Swagger

A documentação interativa da API está disponível em:

**🔗 Swagger UI:** http://localhost:8080/swagger-ui.html

**🔗 API JSON:** http://localhost:8080/v3/api-docs

### 📋 Schemas Definidos

#### TicketRequestDTO
```yaml
titulo: string (required)
descricao: string
status: string (required)
clienteId: integer (required)
```

#### Cliente
```yaml
id: integer
nome: string (required)
email: string
telefone: string
```

#### BatchProcess
```yaml
id: integer
fileName: string
startTime: date-time
endTime: date-time
totalRecords: integer
processedRecords: integer
successCount: integer
errorCount: integer
status: enum [Processando, Concluído, Falhou, Cancelado]
errorMessage: string
progressPercentage: double
```

## ⚙️ Configuração

### Banco de Dados
O sistema suporta dois bancos de dados:

**H2 (Desenvolvimento):**
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

**MySQL (Produção):**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ticketdb
spring.datasource.username=root
spring.datasource.password=password
```

### Processamento de Arquivos
- Formatos suportados: `.xlsx`, `.xls`
- Processamento assíncrono
- Atualização em tempo real via WebSocket

## 🚀 Como Executar

1. **Clone o repositório**
   ```bash
   git clone <repository-url>
   cd ticket-management-system
   ```

2. **Configure o banco de dados** (opcional para H2)
   ```bash
   # Edite application.properties para MySQL se necessário
   ```

3. **Execute a aplicação**
   ```bash
   mvn spring-boot:run
   ```

4. **Acesse a aplicação**
   ```
   http://localhost:8080
   ```

5. **Acesse a documentação**
   ```
   http://localhost:8080/swagger-ui.html
   ```

## 🧪 Testes

Execute os testes com:
```bash
mvn test
```

## 📈 Funcionalidades Avançadas

- ✅ Upload e processamento de arquivos Excel
- ✅ Interface web com Thymeleaf
- ✅ API REST documentada com Swagger
- ✅ WebSocket para atualizações em tempo real
- ✅ Processamento assíncrono
- ✅ Suporte a múltiplos bancos de dados
- ✅ Validação de dados
- ✅ Gestão de erros

## 🔧 Desenvolvimento

Para desenvolvimento com hot reload:
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
```

## 📝 Licença

Este projeto está sob licença MIT. Veja o arquivo LICENSE para mais detalhes.

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

---

**Desenvolvido com ❤️ usando Spring Boot e Java 21**