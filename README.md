# Desafio Técnico Di2Win

## Visão Geral do Projeto
Este projeto é a implementação de um sistema de **Conta Online**, conforme o desafio proposto pela **Di2win**.  
A solução foi desenvolvida com foco em boas práticas de arquitetura, segurança de dados e extensibilidade, utilizando o ecossistema **Spring Boot** para oferecer uma **API REST** robusta e funcional.

---

## Arquitetura da Solução
A aplicação segue o padrão de **arquitetura em camadas**, garantindo a separação de responsabilidades e a facilidade de manutenção:

- **Controller**: Camada de entrada que lida com as requisições HTTP, utilizando DTOs (Data Transfer Objects) para garantir um contrato de dados claro e seguro.  
- **Service**: Camada de lógica de negócio, onde as regras de saque, depósito e validação são centralizadas.  
- **Repository**: Camada de persistência que interage com o banco de dados.  

---

## Tecnologias Utilizadas
- **Java 17**: Linguagem de programação robusta e de alto desempenho.  
- **Spring Boot 3.1.6**: Framework que simplifica o desenvolvimento de aplicações Java.  
- **Spring Data JPA**: Ferramenta para gerenciar a persistência de dados em bancos de dados relacionais.  
- **H2 Database**: Banco de dados em memória utilizado para testes e desenvolvimento rápido.  
- **PostgreSQL**: Driver de banco de dados relacional para um ambiente de produção.  
- **Maven**: Ferramenta de gerenciamento de dependências e automação de build.  
- **JUnit 5 & Mockito**: Frameworks para a escrita de testes automatizados.  

---

## Funcionalidades e Rotas da API
A API oferece os seguintes serviços para **gerenciamento de clientes e contas**:

**Swagger UI:**
Acesse http://localhost:8080/swagger-ui.html no seu navegador para ver a documentação interativa e testar os endpoints diretamente.

### Criar Cliente (POST)
`POST /clientes`

**Exemplo de requisição (JSON):**
```json
{
  "nome": "João da Silva",
  "cpf": "12345678901",
  "dataNascimento": "1990-01-01"
}
````

**Respostas:**

* `201 Created` → Cliente criado com sucesso
* `400 Bad Request` → Falha na validação (ex: CPF inválido)

---

### Remover Cliente (DELETE)

`DELETE /clientes/{id}`

**Respostas:**

* `204 No Content` → Cliente removido
* `404 Not Found` → Cliente não encontrado

---

### Criar Conta (POST)

`POST /contas`

**Exemplo de requisição (JSON):**

```json
{
  "cpfCliente": "12345678901"
}
```

**Respostas:**

* `201 Created` → Conta criada com sucesso
* `404 Not Found` → Cliente não encontrado

---

### Depósito (POST)

`POST /contas/{numero}/deposito`

**Exemplo de requisição (JSON):**

```json
{
  "valor": 100.00
}
```

**Respostas:**

* `200 OK` → Depósito realizado
* `400 Bad Request` → Valor inválido ou conta bloqueada

---

### Saque (POST)

`POST /contas/{numero}/saque`

**Exemplo de requisição (JSON):**

```json
{
  "valor": 50.00
}
```

**Respostas:**

* `200 OK` → Saque realizado
* `400 Bad Request` → Saldo insuficiente ou limite excedido

---

### Consultar Saldo (GET)

`GET /contas/{numero}/saldo`

**Respostas:**

* `200 OK` → Retorna saldo atual
* `404 Not Found` → Conta não encontrada

---

### Extrato de Transações (GET)

`GET /contas/{numero}/extrato?inicio=2025-01-01T00:00:00&fim=2025-12-31T23:59:59`

**Respostas:**

* `200 OK` → Retorna lista de transações no período
* `404 Not Found` → Conta não encontrada

---

### Bloquear Conta (POST)

`POST /contas/{numero}/bloquear`

**Respostas:**

* `204 No Content` → Conta bloqueada

---

### Desbloquear Conta (POST)

`POST /contas/{numero}/desbloquear`

**Respostas:**

* `204 No Content` → Conta desbloqueada


---
## Diferenciais
- **Testes Automatizados**: Inclui testes unitários para a lógica de negócio, garantindo a robustez das regras de validação e operações financeiras.  
- **Documentação Interativa**: A API é auto-documentada com **Swagger UI**, acessível em:  
  [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
  Isso permite que qualquer desenvolvedor visualize e teste as rotas de forma intuitiva.  
- **Modelagem de Dados**: O `schema.sql` foi criado com índices e tipos de dados otimizados para desempenho.  

---

## Como Executar o Projeto

### Requisitos
- **Java 17** ou superior.  
- Acesso a um terminal (**CMD, PowerShell, etc.**).  

### Instruções
1. Clone o projeto do GitHub:
   ```bash
   git clone https://github.com/Kaiquegb/Di2WinDesafio.git


2. Navegue até o diretório do projeto:

   ```bash
   cd Di2WinDesafio
   ```

3. Inicie a aplicação usando o **Maven Wrapper**:

   ```bash
   ./mvnw spring-boot:run
   ```

   *(No Windows, use `mvnw.cmd spring-boot:run`)*

A aplicação será iniciada e o **banco de dados H2** será configurado automaticamente.






