# Monolito (API Principal)

API central do sistema, desenvolvida com **Spring Boot**, responsável por praticamente todas as funcionalidades da aplicação.  
Gerencia autenticação, catálogo, edição de documentos e fluxo de revisão.  
A única função externa é a geração de PDFs, delegada a API **Kurush**.

---

## Guia de Uso
[Acesso Online](https://s13.api.br/swagger-ui/index.html)

### Swagger
A documentação e testes dos endpoints estão disponíveis em:

```
http://localhost:13000/swagger-ui/index.html
```

---

## Autenticação

Antes de usar qualquer endpoint, é necessário realizar login em:

```
POST /api/user/auth/login
```

A API utiliza **Flyway** para criar as tabelas iniciais e inserir um usuário admin por padrão.

Exemplo de login:

```json
{
  "username": "admin",
  "password": "123456"
}
```

A resposta retornará um **Bearer Token**, que deve ser enviado no header:

```
Authorization: Bearer <token>
```

ou configurado diretamente no Swagger.

---

## Principais Grupos de Endpoints

- **user** – Operações para gerenciamento de usuários  
- **catalog** – Operações para gerenciamento e navegação do catálogo  
- **editor** – Criação e edição de documentos de especificação  
- **revision** – Ações relacionadas ao fluxo de revisão e aprovação de documentos  

## Docker

### Executando o Container

```bash
docker run -d \
  --name monolito_api \
  -p 13000:13000 \
  -e POSTGRES_URL="jdbc:postgresql://monolito_db:5432/monolito" \
  -e POSTGRES_USER="postgres" \
  -e POSTGRES_PASSWORD="postgres" \
  -e SERVER_PORT=13000 \
  -e MONGO_URI="mongodb://admin:admin@mongo_db:27017/monolito?authSource=admin" \
  -e ALLOWED_ORIGINS="*" \
  -e SECRET="secret-0000000000000000000000000000" \
  yuri000/monolito_api:latest
```

### Variáveis de Ambiente

```bash
export DB_HOST=...
export DB_USER=...
export DB_PASS=...
```

---

## Ambiente Local

### Pré-requisitos
- **Java 21+**
- **Maven 3.8+**
- **PostgreSQL** em execução
- **MongoDB** em execução
- Docker (opcional)

### Configuração
Ajuste os valores necessários em:

```
src/main/resources/application.yml
```

### Subindo serviços auxiliares (Postgres, Mongo, etc.)

```bash
docker-compose up -d
```

### Executando a aplicação

```bash
./mvnw spring-boot:run
```

### Logs

```bash
tail -f logs/monolito.log
```

---
