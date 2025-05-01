# Auth JWT

Projeto para controle de acesso para micro serviços, tecnologia usada:

- Java 21
- Spring boot 3.4.5
- spring security
- JPA
- H2
- Lombok

Pre-requisitos:
- Jdk 21
- Maven 3.9 ou superior
- Postman

Start do projeto:
- Via cmd:
  - mvn clean install
  - mvn spring-boot:run
  
Testar Projeto: 
- Importar no Postman a collection: auth-jwt-jdk21.postman_collection.json localizada na raiz do projeto dentro do diretorio collection. 

Configs:
- No arquivo application.properties:
  - A var security.jwt.secret-key define a secret key, deve ser um hash HMAC do tipo string de 256 bits
    - Para gerar outra chave pode ser usado o site: https://www.devglan.com/online-tools/hmac-sha256-online
  - A var security.jwt.expiration-time permite definir o tempo de validade do token em milis.
    - Para fins de teste por padrao está setado em 60k (um minuto).
  
  