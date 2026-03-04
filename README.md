<<<<<<< HEAD
# API KASolution – Projeto ToDo

API REST para gestão de tarefas com perfis **Gerente** e **Desenvolvedor**: autenticação JWT, CRUD de tarefas, dashboard e fluxo de execução (iniciar, pausar, finalizar).

---

## Índice

- [Pré-requisitos](#pré-requisitos)
- [Testar com banco H2 (memória)](#testar-com-banco-h2-memória)
- [Ordem das rotas para verificar o funcionamento total](#ordem-das-rotas-para-verificar-o-funcionamento-total)
- [Rotas da API](#rotas-da-api)
- [Modelos de request/response (JSON)](#modelos-de-requestresponse-json)
- [Respostas de erro padronizadas](#respostas-de-erro-padronizadas)
- [Enums utilizados](#enums-utilizados)

---

## Pré-requisitos

- **Java 21**
- **Maven** (ou use o wrapper `./mvnw` / `mvnw.cmd`)

---

## Testar com banco H2 (memória)

Para testar a API sem PostgreSQL, use o banco em memória H2.

### 1. Adicionar dependência H2

No `pom.xml`, na seção `<dependencies>`, adicione:

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 2. Criar perfil de configuração H2

Crie o arquivo **`src/main/resources/application-h2.yml`** com o conteúdo abaixo:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:kasolution;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update

app:
  jwt:
    # Use uma chave longa e aleatória em testes (mín. 256 bits para HS256)
    secret: ${APP_JWT_SECRET:}
    expiration-minutes: 720
    issuer: kasolution-api
  
  springdoc:
    swagger-ui:
      path: /swagger-ui.html
    api-docs:
      path: /v3/api-docs  
```

### 3. Subir a aplicação com perfil H2

No terminal, na raiz do projeto:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

No Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=h2"
```

Ou definindo variável de ambiente:

```bash
set SPRING_PROFILES_ACTIVE=h2
./mvnw spring-boot:run
```

### 4. Console H2 (opcional)

Com o perfil `h2` ativo, o console fica disponível em:

- **URL:** http://localhost:8080/h2-console  
- **JDBC URL:** `jdbc:h2:mem:kasolution`  
- **User:** `sa`  
- **Password:** (deixe em branco)

A API passa a responder em **http://localhost:8080** (porta padrão).

---

## Ordem das rotas para verificar o funcionamento total

Siga esta sequência para validar a API de ponta a ponta usando H2:

| Ordem | Rota | Objetivo |
|-------|------|----------|
| **1** | `POST /api/gerente/registro` | Criar o primeiro usuário (gerente). Guardar o retorno se quiser conferir o `id`. |
| **2** | `POST /api/auth/login` | Fazer login com o gerente. **Guarde o `token`** para usar no header `Authorization: Bearer <token>` nas próximas chamadas. |
| **3** | `POST /api/gerente/registro/desenvolvedor` | Com o token do gerente, cadastrar um desenvolvedor. Confirma que o gerente está autenticado e que a criação de desenvolvedor funciona. |
| **4** | `POST /api/tarefas/criar` | Com o token do gerente, criar uma tarefa. Confirma CRUD de tarefas e permissão de gerente. **Guarde o `id` da tarefa** retornada. |
| **5** | `GET /api/tarefas/dashboard` | Com o token do gerente (ou do desenvolvedor vinculado a esse gerente), listar tarefas ativas. Confirma que a tarefa criada aparece. |
| **6** | `POST /api/tarefas/{tarefaId}/iniciar` | Com o token do desenvolvedor, iniciar a tarefa (status passa a DOING). Confirma fluxo de execução. |
| **7** | `POST /api/tarefas/{tarefaId}/pausar` | Com o token do mesmo desenvolvedor, pausar a tarefa (status PAUSED). |
| **8** | `POST /api/tarefas/{tarefaId}/finalizar` | Com o token do desenvolvedor, finalizar a tarefa enviando detalhes (body texto). Status passa a DONE. |
| **9** | `PUT /api/tarefas/editar/{tarefaId}` | Com token de gerente ou desenvolvedor, editar título/descrição/prazo de uma tarefa. |
| **10** | `DELETE /api/tarefas/deletar/{tarefaId}/` | Com token de gerente, soft-delete da tarefa (parâmetro `confirmada` se já tiver progresso). |

Após isso, a API estará validada em: autenticação, perfis (gerente/desenvolvedor), criação de tarefas, dashboard, fluxo iniciar → pausar → finalizar, edição e exclusão.

---

## Rotas da API

Base URL: **http://localhost:8080**

Rotas **públicas** (sem header `Authorization`):

- `POST /api/auth/login`
- `POST /api/gerente/registro`
- `POST /api/desenvolvedor/registro` (registro aberto de desenvolvedor)
- `GET /swagger-ui.html`, `/v3/api-docs/**`
- `GET /h2-console/**` (quando perfil H2 ativo)

Demais rotas exigem: **`Authorization: Bearer <token>`** (token obtido em `/api/auth/login`).

---

### Autenticação

#### `POST /api/auth/login`

Autentica e retorna o token JWT e dados do usuário.

**Request (JSON):**

```json
{
  "email": "gerente@email.com",
  "senha": "senha123"
}
```

**Response (200):** Ver [AuthResponse](#authresponse).

**Erros:** 403 – credenciais inválidas ou usuário inativo.

---

### Gerente

#### `POST /api/gerente/registro`

Cadastra um novo **gerente**. Rota pública.

**Request (JSON):** Ver [UsuarioRequest](#usuariorequest).

**Response (200):** Ver [UsuarioResponse](#usuarioresponse) (gerente; `gerenteid` será `null`).

**Erros:** 403 – email já cadastrado. 400 – validação (nome, email, senha).

---

#### `POST /api/gerente/registro/desenvolvedor`

Cadastra um **desenvolvedor** vinculado ao gerente logado. Requer token de **GERENTE**.

**Headers:** `Authorization: Bearer <token>`

**Request (JSON):** Ver [UsuarioRequest](#usuariorequest).

**Response (200):** Ver [UsuarioResponse](#usuarioresponse) (desenvolvedor; `gerenteid` preenchido).

**Erros:** 403 – não é gerente ou email já existe. 400 – validação.

---

### Tarefas (CRUD e dashboard)

#### `POST /api/tarefas/criar`

Cria uma nova tarefa. Requer token de **GERENTE**.

**Headers:** `Authorization: Bearer <token>`

**Request (JSON):** Ver [TarefaRequest](#tarefarequest).

**Response (200):** Ver [TarefaResponse](#tarefaresponse).

**Erros:** 403 – usuário não é gerente. 400 – validação.

---

#### `PUT /api/tarefas/editar/{tarefaId}`

Edita título, descrição e prazo da tarefa. Requer token de **GERENTE** ou **DESENVOLVEDOR**.

**Headers:** `Authorization: Bearer <token>`

**Path:** `tarefaId` = UUID da tarefa.

**Request (JSON):** Ver [TarefaRequest](#tarefarequest) (campos utilizados na edição: titulo, descricao, prazoFinal, etc.).

**Response (200):** Objeto com `id`, `titulo`, `descricao`, `tarefaStatus`, `prazoFinal` (mesmo formato parcial de [TarefaResponse](#tarefaresponse)).

**Erros:** 404 – tarefa não encontrada. 403 – não autorizado.

---

#### `DELETE /api/tarefas/deletar/{tarefaId}/`

Soft-delete da tarefa (marca como inativa). Requer token de **GERENTE**. Só o gerente dono da tarefa pode excluir.

**Headers:** `Authorization: Bearer <token>`

**Path:** `tarefaId` = UUID da tarefa.

**Query (opcional):** `confirmada=true` – necessário se a tarefa já tiver progresso (ex.: DOING/PAUSED) e mesmo assim quiser excluir.

**Response (204):** Sem corpo.

**Erros:** 404 – tarefa não encontrada. 403 – não é gerente ou não é dono. 409 – tarefa concluída (DONE) não pode ser excluída; ou tarefa em progresso sem `confirmada=true`.

---

#### `GET /api/tarefas/dashboard`

Lista tarefas **ativas** do gerente (ou do gerente ao qual o desenvolvedor está vinculado). Requer token de **GERENTE** ou **DESENVOLVEDOR**.

**Headers:** `Authorization: Bearer <token>`

**Response (200):** Lista de [TarefaResponse](#tarefaresponse).

**Erros:** 403 – não autorizado.

---

### Execução de tarefas (iniciar, pausar, finalizar)

#### `POST /api/tarefas/{tarefaId}/iniciar`

Coloca a tarefa em execução (status **DOING**). Requer token de **GERENTE** ou **DESENVOLVEDOR**.

**Headers:** `Authorization: Bearer <token>`

**Path:** `tarefaId` = UUID da tarefa.

**Response (204):** Sem corpo.

**Erros:** 404 – tarefa não encontrada. 409 – tarefa já DOING ou já DONE.

---

#### `POST /api/tarefas/{tarefaId}/pausar`

Pausa a tarefa (status **PAUSED**). Requer token de **GERENTE** ou **DESENVOLVEDOR**.

**Headers:** `Authorization: Bearer <token>`

**Path:** `tarefaId` = UUID da tarefa.

**Response (204):** Sem corpo.

**Erros:** 404 – tarefa ou alocação não encontrada. 409 – tarefa não está DOING.

---

#### `POST /api/tarefas/{tarefaId}/finalizar`

Finaliza a tarefa (status **DONE**) e registra detalhes da execução. Requer token de **GERENTE** ou **DESENVOLVEDOR**.

**Headers:** `Authorization: Bearer <token>`

**Path:** `tarefaId` = UUID da tarefa.

**Request (body):** Texto plano (string) com os detalhes da execução. Exemplo no Postman/Insomnia: body **raw** tipo **Text** com algo como `Implementação concluída e revisada`.

**Response (204):** Sem corpo.

**Erros:** 404 – tarefa ou alocação não encontrada. 409 – tarefa ainda está TODO (precisa ter sido iniciada antes).

---

## Modelos de request/response (JSON)

### AuthRequest

Usado em **POST /api/auth/login**.

```json
{
  "email": "usuario@email.com",
  "senha": "minhasenha"
}
```

- `email`: string, obrigatório, formato e-mail.
- `senha`: string, obrigatório.

---

### AuthResponse

Retornado em **POST /api/auth/login** (200).

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuarioResponse": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "nome": "João Gerente",
    "email": "gerente@email.com",
    "role": "GERENTE",
    "gerenteid": null,
    "ativo": true
  }
}
```

- `token`: string JWT para enviar em `Authorization: Bearer <token>`.
- `usuarioResponse`: ver [UsuarioResponse](#usuarioresponse). Para gerente, `gerenteid` é `null`; para desenvolvedor, é o UUID do gerente.

---

### UsuarioRequest

Usado em **POST /api/gerente/registro** e **POST /api/gerente/registro/desenvolvedor**.

```json
{
  "nome": "Maria Dev",
  "email": "maria@email.com",
  "senha": "senha123"
}
```

- `nome`: string, obrigatório.
- `email`: string, obrigatório, formato e-mail.
- `senha`: string, obrigatório, entre 6 e 100 caracteres.

---

### UsuarioResponse

Retornado em login e nos registros de gerente/desenvolvedor.

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nome": "Maria Dev",
  "email": "maria@email.com",
  "role": "DESENVOLVEDOR",
  "gerenteid": "660e8400-e29b-41d4-a716-446655440001",
  "ativo": true
}
```

- `id`: UUID.
- `gerenteid`: UUID do gerente (null para gerente ou admin).
- `role`: um de `ADMIN`, `GERENTE`, `DESENVOLVEDOR`.

---

### CategoriaRequest

Usado **dentro** de [TarefaRequest](#tarefarequest).

```json
{
  "nome": "Backend",
  "hexadecimal": "#3B82F6"
}
```

- `nome`: string, obrigatório.
- `hexadecimal`: string, obrigatório (cor, ex.: #3B82F6).

---

### TarefaRequest

Usado em **POST /api/tarefas/criar** e **PUT /api/tarefas/editar/{tarefaId}**.

```json
{
  "titulo": "Implementar endpoint de estatísticas",
  "descricao": "Criar GET /api/tarefas/estatisticas com percentual atrasadas e por categoria.",
  "tarefaStatus": "TODO",
  "prazoFinal": "2025-12-31T23:59:59Z",
  "prioridade": "ALTA",
  "categoria": {
    "nome": "Backend",
    "hexadecimal": "#3B82F6"
  },
  "etiquetas": ["api", "estatisticas", "backend"]
}
```

- `titulo`, `descricao`: strings, obrigatórios.
- `tarefaStatus`: um de [TarefaStatus](#tarefastatus).
- `prazoFinal`: ISO-8601 (ex.: `2025-12-31T23:59:59Z`).
- `prioridade`: um de [PrioridadeType](#prioridadetype).
- `categoria`: objeto [CategoriaRequest](#categoriarequest).
- `etiquetas`: array de strings (opcional); nomes de etiquetas (criadas se não existirem).

---

### TarefaResponse

Retornado em criação de tarefa, edição e **GET /api/tarefas/dashboard**.

```json
{
  "id": "770e8400-e29b-41d4-a716-446655440002",
  "titulo": "Implementar endpoint de estatísticas",
  "descricao": "Criar GET /api/tarefas/estatisticas...",
  "tarefaStatus": "TODO",
  "prazoFinal": "2025-12-31T23:59:59Z",
  "prioridade": "ALTA",
  "categoria": {
    "id": 1,
    "nome": "Backend",
    "hexadecimal": "#3B82F6"
  },
  "etiquetas": [
    { "id": 1, "descricao": "api" },
    { "id": 2, "descricao": "backend" }
  ]
}
```

- `id`: UUID da tarefa.
- `categoria`: [CategoriaResponse](#categoriaresponse).
- `etiquetas`: array de [EtiquetaResponse](#etiquetaresponse).

---

### CategoriaResponse

```json
{
  "id": 1,
  "nome": "Backend",
  "hexadecimal": "#3B82F6"
}
```

---

### EtiquetaResponse

```json
{
  "id": 1,
  "descricao": "api"
}
```

---

## Respostas de erro padronizadas

Em caso de erro de negócio ou validação, a API retorna JSON no formato abaixo (DTO `ErroResponse`).

### ErroResponse

```json
{
  "timestamp": "2025-02-28T14:30:00.123Z",
  "status": 403,
  "erro": "Credenciais inválidas",
  "mensagem": "Senha não confere",
  "path": "/api/auth/login"
}
```

- `timestamp`: ISO-8601.
- `status`: código HTTP (ex.: 400, 403, 404, 409).
- `erro`: tipo/categoria do erro.
- `mensagem`: detalhe para o usuário.
- `path`: URI da requisição.

Exemplos de uso no projeto: credenciais inválidas (403), acesso negado (403), não encontrado (404), conflito em regra de tarefa (409), violação de integridade (409).

---

## Enums utilizados

### Role

Valores: `ADMIN`, `GERENTE`, `DESENVOLVEDOR`.

---

### TarefaStatus

Valores: `TODO`, `DOING`, `PAUSED`, `REVIEW`, `DONE`.

Fluxo típico: **TODO** → **DOING** (iniciar) → **PAUSED** (pausar) → **DOING** (iniciar de novo) → **DONE** (finalizar).

---

### PrioridadeType

Valores: `URGENTE`, `ALTA`, `MEDIA`, `BAIXA`, `NORMAL`.

---

## Documentação interativa (Swagger)

Com a aplicação rodando, a documentação Swagger está em:

- **Swagger UI:** http://localhost:8080/swagger-ui.html  
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs  

Útil para testar as rotas diretamente no navegador (incluindo envio de token JWT quando configurado).
=======
# API KASolution – Projeto ToDo

API REST para gestão de tarefas com perfis **Gerente** e **Desenvolvedor**: autenticação JWT, CRUD de tarefas, dashboard e fluxo de execução (iniciar, pausar, finalizar).

---

## Índice

- [Pré-requisitos](#pré-requisitos)
- [Testar com banco H2 (memória)](#testar-com-banco-h2-memória)
- [Ordem das rotas para verificar o funcionamento total](#ordem-das-rotas-para-verificar-o-funcionamento-total)
- [Rotas da API](#rotas-da-api)
- [Modelos de request/response (JSON)](#modelos-de-requestresponse-json)
- [Respostas de erro padronizadas](#respostas-de-erro-padronizadas)
- [Enums utilizados](#enums-utilizados)

---

## Pré-requisitos

- **Java 21**
- **Maven** (ou use o wrapper `./mvnw` / `mvnw.cmd`)

---

## Testar com banco H2 (memória)

Para testar a API sem PostgreSQL, use o banco em memória H2.

### 1. Adicionar dependência H2

No `pom.xml`, na seção `<dependencies>`, adicione:

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 2. Criar perfil de configuração H2

Crie o arquivo **`src/main/resources/application-h2.yml`** com o conteúdo abaixo:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:kasolution;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update

app:
  jwt:
    # Use uma chave longa e aleatória em testes (mín. 256 bits para HS256)
    secret: ${APP_JWT_SECRET:}
    expiration-minutes: 720
    issuer: kasolution-api
  
  springdoc:
    swagger-ui:
      path: /swagger-ui.html
    api-docs:
      path: /v3/api-docs  
```

### 3. Subir a aplicação com perfil H2

No terminal, na raiz do projeto:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

No Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=h2"
```

Ou definindo variável de ambiente:

```bash
set SPRING_PROFILES_ACTIVE=h2
./mvnw spring-boot:run
```

### 4. Console H2 (opcional)

Com o perfil `h2` ativo, o console fica disponível em:

- **URL:** http://localhost:8080/h2-console  
- **JDBC URL:** `jdbc:h2:mem:kasolution`  
- **User:** `sa`  
- **Password:** (deixe em branco)

A API passa a responder em **http://localhost:8080** (porta padrão).

---

## Ordem das rotas para verificar o funcionamento total

Siga esta sequência para validar a API de ponta a ponta usando H2:

| Ordem | Rota | Objetivo |
|-------|------|----------|
| **1** | `POST /api/gerente/registro` | Criar o primeiro usuário (gerente). Guardar o retorno se quiser conferir o `id`. |
| **2** | `POST /api/auth/login` | Fazer login com o gerente. **Guarde o `token`** para usar no header `Authorization: Bearer <token>` nas próximas chamadas. |
| **3** | `POST /api/gerente/registro/desenvolvedor` | Com o token do gerente, cadastrar um desenvolvedor. Confirma que o gerente está autenticado e que a criação de desenvolvedor funciona. |
| **4** | `POST /api/tarefas/criar` | Com o token do gerente, criar uma tarefa. Confirma CRUD de tarefas e permissão de gerente. **Guarde o `id` da tarefa** retornada. |
| **5** | `GET /api/tarefas/dashboard` | Com o token do gerente (ou do desenvolvedor vinculado a esse gerente), listar tarefas ativas. Confirma que a tarefa criada aparece. |
| **6** | `POST /api/tarefas/{tarefaId}/iniciar` | Com o token do desenvolvedor, iniciar a tarefa (status passa a DOING). Confirma fluxo de execução. |
| **7** | `POST /api/tarefas/{tarefaId}/pausar` | Com o token do mesmo desenvolvedor, pausar a tarefa (status PAUSED). |
| **8** | `POST /api/tarefas/{tarefaId}/finalizar` | Com o token do desenvolvedor, finalizar a tarefa enviando detalhes (body texto). Status passa a DONE. |
| **9** | `PUT /api/tarefas/editar/{tarefaId}` | Com token de gerente ou desenvolvedor, editar título/descrição/prazo de uma tarefa. |
| **10** | `DELETE /api/tarefas/deletar/{tarefaId}/` | Com token de gerente, soft-delete da tarefa (parâmetro `confirmada` se já tiver progresso). |

Após isso, a API estará validada em: autenticação, perfis (gerente/desenvolvedor), criação de tarefas, dashboard, fluxo iniciar → pausar → finalizar, edição e exclusão.

---

## Rotas da API

Base URL: **http://localhost:8080**

Rotas **públicas** (sem header `Authorization`):

- `POST /api/auth/login`
- `POST /api/gerente/registro`
- `POST /api/desenvolvedor/registro` (registro aberto de desenvolvedor)
- `GET /swagger-ui.html`, `/v3/api-docs/**`
- `GET /h2-console/**` (quando perfil H2 ativo)

Demais rotas exigem: **`Authorization: Bearer <token>`** (token obtido em `/api/auth/login`).

---

### Autenticação

#### `POST /api/auth/login`

Autentica e retorna o token JWT e dados do usuário.

**Request (JSON):**

```json
{
  "email": "gerente@email.com",
  "senha": "senha123"
}
```

**Response (200):** Ver [AuthResponse](#authresponse).

**Erros:** 403 – credenciais inválidas ou usuário inativo.

---

### Gerente

#### `POST /api/gerente/registro`

Cadastra um novo **gerente**. Rota pública.

**Request (JSON):** Ver [UsuarioRequest](#usuariorequest).

**Response (200):** Ver [UsuarioResponse](#usuarioresponse) (gerente; `gerenteid` será `null`).

**Erros:** 403 – email já cadastrado. 400 – validação (nome, email, senha).

---

#### `POST /api/gerente/registro/desenvolvedor`

Cadastra um **desenvolvedor** vinculado ao gerente logado. Requer token de **GERENTE**.

**Headers:** `Authorization: Bearer <token>`

**Request (JSON):** Ver [UsuarioRequest](#usuariorequest).

**Response (200):** Ver [UsuarioResponse](#usuarioresponse) (desenvolvedor; `gerenteid` preenchido).

**Erros:** 403 – não é gerente ou email já existe. 400 – validação.

---

### Tarefas (CRUD e dashboard)

#### `POST /api/tarefas/criar`

Cria uma nova tarefa. Requer token de **GERENTE**.

**Headers:** `Authorization: Bearer <token>`

**Request (JSON):** Ver [TarefaRequest](#tarefarequest).

**Response (200):** Ver [TarefaResponse](#tarefaresponse).

**Erros:** 403 – usuário não é gerente. 400 – validação.

---

#### `PUT /api/tarefas/editar/{tarefaId}`

Edita título, descrição e prazo da tarefa. Requer token de **GERENTE** ou **DESENVOLVEDOR**.

**Headers:** `Authorization: Bearer <token>`

**Path:** `tarefaId` = UUID da tarefa.

**Request (JSON):** Ver [TarefaRequest](#tarefarequest) (campos utilizados na edição: titulo, descricao, prazoFinal, etc.).

**Response (200):** Objeto com `id`, `titulo`, `descricao`, `tarefaStatus`, `prazoFinal` (mesmo formato parcial de [TarefaResponse](#tarefaresponse)).

**Erros:** 404 – tarefa não encontrada. 403 – não autorizado.

---

#### `DELETE /api/tarefas/deletar/{tarefaId}/`

Soft-delete da tarefa (marca como inativa). Requer token de **GERENTE**. Só o gerente dono da tarefa pode excluir.

**Headers:** `Authorization: Bearer <token>`

**Path:** `tarefaId` = UUID da tarefa.

**Query (opcional):** `confirmada=true` – necessário se a tarefa já tiver progresso (ex.: DOING/PAUSED) e mesmo assim quiser excluir.

**Response (204):** Sem corpo.

**Erros:** 404 – tarefa não encontrada. 403 – não é gerente ou não é dono. 409 – tarefa concluída (DONE) não pode ser excluída; ou tarefa em progresso sem `confirmada=true`.

---

#### `GET /api/tarefas/dashboard`

Lista tarefas **ativas** do gerente (ou do gerente ao qual o desenvolvedor está vinculado). Requer token de **GERENTE** ou **DESENVOLVEDOR**.

**Headers:** `Authorization: Bearer <token>`

**Response (200):** Lista de [TarefaResponse](#tarefaresponse).

**Erros:** 403 – não autorizado.

---

### Execução de tarefas (iniciar, pausar, finalizar)

#### `POST /api/tarefas/{tarefaId}/iniciar`

Coloca a tarefa em execução (status **DOING**). Requer token de **GERENTE** ou **DESENVOLVEDOR**.

**Headers:** `Authorization: Bearer <token>`

**Path:** `tarefaId` = UUID da tarefa.

**Response (204):** Sem corpo.

**Erros:** 404 – tarefa não encontrada. 409 – tarefa já DOING ou já DONE.

---

#### `POST /api/tarefas/{tarefaId}/pausar`

Pausa a tarefa (status **PAUSED**). Requer token de **GERENTE** ou **DESENVOLVEDOR**.

**Headers:** `Authorization: Bearer <token>`

**Path:** `tarefaId` = UUID da tarefa.

**Response (204):** Sem corpo.

**Erros:** 404 – tarefa ou alocação não encontrada. 409 – tarefa não está DOING.

---

#### `POST /api/tarefas/{tarefaId}/finalizar`

Finaliza a tarefa (status **DONE**) e registra detalhes da execução. Requer token de **GERENTE** ou **DESENVOLVEDOR**.

**Headers:** `Authorization: Bearer <token>`

**Path:** `tarefaId` = UUID da tarefa.

**Request (body):** Texto plano (string) com os detalhes da execução. Exemplo no Postman/Insomnia: body **raw** tipo **Text** com algo como `Implementação concluída e revisada`.

**Response (204):** Sem corpo.

**Erros:** 404 – tarefa ou alocação não encontrada. 409 – tarefa ainda está TODO (precisa ter sido iniciada antes).

---

## Modelos de request/response (JSON)

### AuthRequest

Usado em **POST /api/auth/login**.

```json
{
  "email": "usuario@email.com",
  "senha": "minhasenha"
}
```

- `email`: string, obrigatório, formato e-mail.
- `senha`: string, obrigatório.

---

### AuthResponse

Retornado em **POST /api/auth/login** (200).

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuarioResponse": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "nome": "João Gerente",
    "email": "gerente@email.com",
    "role": "GERENTE",
    "gerenteid": null,
    "ativo": true
  }
}
```

- `token`: string JWT para enviar em `Authorization: Bearer <token>`.
- `usuarioResponse`: ver [UsuarioResponse](#usuarioresponse). Para gerente, `gerenteid` é `null`; para desenvolvedor, é o UUID do gerente.

---

### UsuarioRequest

Usado em **POST /api/gerente/registro** e **POST /api/gerente/registro/desenvolvedor**.

```json
{
  "nome": "Maria Dev",
  "email": "maria@email.com",
  "senha": "senha123"
}
```

- `nome`: string, obrigatório.
- `email`: string, obrigatório, formato e-mail.
- `senha`: string, obrigatório, entre 6 e 100 caracteres.

---

### UsuarioResponse

Retornado em login e nos registros de gerente/desenvolvedor.

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nome": "Maria Dev",
  "email": "maria@email.com",
  "role": "DESENVOLVEDOR",
  "gerenteid": "660e8400-e29b-41d4-a716-446655440001",
  "ativo": true
}
```

- `id`: UUID.
- `gerenteid`: UUID do gerente (null para gerente ou admin).
- `role`: um de `ADMIN`, `GERENTE`, `DESENVOLVEDOR`.

---

### CategoriaRequest

Usado **dentro** de [TarefaRequest](#tarefarequest).

```json
{
  "nome": "Backend",
  "hexadecimal": "#3B82F6"
}
```

- `nome`: string, obrigatório.
- `hexadecimal`: string, obrigatório (cor, ex.: #3B82F6).

---

### TarefaRequest

Usado em **POST /api/tarefas/criar** e **PUT /api/tarefas/editar/{tarefaId}**.

```json
{
  "titulo": "Implementar endpoint de estatísticas",
  "descricao": "Criar GET /api/tarefas/estatisticas com percentual atrasadas e por categoria.",
  "tarefaStatus": "TODO",
  "prazoFinal": "2025-12-31T23:59:59Z",
  "prioridade": "ALTA",
  "categoria": {
    "nome": "Backend",
    "hexadecimal": "#3B82F6"
  },
  "etiquetas": ["api", "estatisticas", "backend"]
}
```

- `titulo`, `descricao`: strings, obrigatórios.
- `tarefaStatus`: um de [TarefaStatus](#tarefastatus).
- `prazoFinal`: ISO-8601 (ex.: `2025-12-31T23:59:59Z`).
- `prioridade`: um de [PrioridadeType](#prioridadetype).
- `categoria`: objeto [CategoriaRequest](#categoriarequest).
- `etiquetas`: array de strings (opcional); nomes de etiquetas (criadas se não existirem).

---

### TarefaResponse

Retornado em criação de tarefa, edição e **GET /api/tarefas/dashboard**.

```json
{
  "id": "770e8400-e29b-41d4-a716-446655440002",
  "titulo": "Implementar endpoint de estatísticas",
  "descricao": "Criar GET /api/tarefas/estatisticas...",
  "tarefaStatus": "TODO",
  "prazoFinal": "2025-12-31T23:59:59Z",
  "prioridade": "ALTA",
  "categoria": {
    "id": 1,
    "nome": "Backend",
    "hexadecimal": "#3B82F6"
  },
  "etiquetas": [
    { "id": 1, "descricao": "api" },
    { "id": 2, "descricao": "backend" }
  ]
}
```

- `id`: UUID da tarefa.
- `categoria`: [CategoriaResponse](#categoriaresponse).
- `etiquetas`: array de [EtiquetaResponse](#etiquetaresponse).

---

### CategoriaResponse

```json
{
  "id": 1,
  "nome": "Backend",
  "hexadecimal": "#3B82F6"
}
```

---

### EtiquetaResponse

```json
{
  "id": 1,
  "descricao": "api"
}
```

---

## Respostas de erro padronizadas

Em caso de erro de negócio ou validação, a API retorna JSON no formato abaixo (DTO `ErroResponse`).

### ErroResponse

```json
{
  "timestamp": "2025-02-28T14:30:00.123Z",
  "status": 403,
  "erro": "Credenciais inválidas",
  "mensagem": "Senha não confere",
  "path": "/api/auth/login"
}
```

- `timestamp`: ISO-8601.
- `status`: código HTTP (ex.: 400, 403, 404, 409).
- `erro`: tipo/categoria do erro.
- `mensagem`: detalhe para o usuário.
- `path`: URI da requisição.

Exemplos de uso no projeto: credenciais inválidas (403), acesso negado (403), não encontrado (404), conflito em regra de tarefa (409), violação de integridade (409).

---

## Enums utilizados

### Role

Valores: `ADMIN`, `GERENTE`, `DESENVOLVEDOR`.

---

### TarefaStatus

Valores: `TODO`, `DOING`, `PAUSED`, `REVIEW`, `DONE`.

Fluxo típico: **TODO** → **DOING** (iniciar) → **PAUSED** (pausar) → **DOING** (iniciar de novo) → **DONE** (finalizar).

---

### PrioridadeType

Valores: `URGENTE`, `ALTA`, `MEDIA`, `BAIXA`, `NORMAL`.

---

## Documentação interativa (Swagger)

Com a aplicação rodando, a documentação Swagger está em:

- **Swagger UI:** http://localhost:8080/swagger-ui.html  
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs  

Útil para testar as rotas diretamente no navegador (incluindo envio de token JWT quando configurado).
>>>>>>> aab8ef3271f2d369950ba22865e86e708fb5f654
