# ContratAI - Sistema Inteligente de Análise e Geração de Contratos

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Instalação e Configuração](#instalação-e-configuração)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Executando o Projeto](#executando-o-projeto)
- [Documentação da API](#documentação-da-api)
- [Endpoints Disponíveis](#endpoints-disponíveis)
- [Exemplos de Uso](#exemplos-de-uso)
- [Autenticação e Autorização](#autenticação-e-autorização)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Contribuindo](#contribuindo)
- [Licença](#licença)
- [Contato](#contato)

## Sobre o Projeto

**ContratAI** é uma API REST desenvolvida como parte da Global Solution da FIAP que utiliza Inteligência Artificial (Google Gemini) para revolucionar a forma como contratos são criados e analisados. O sistema oferece geração automática de contratos personalizados e análise profunda de documentos contratuais, identificando cláusulas críticas e sugerindo melhorias.

### Problema que Resolve

- **Agilidade**: Reduz drasticamente o tempo necessário para redigir contratos
- **Análise Profunda**: Identifica automaticamente cláusulas problemáticas em contratos existentes
- **Proteção**: Ajuda a proteger ambas as partes identificando riscos e desequilíbrios contratuais
- **Sugestões Inteligentes**: Fornece recomendações para melhorar cláusulas complexas
- **Gestão Centralizada**: Armazena e gerencia todos os contratos em um único sistema

## Funcionalidades

### Geração Automática de Contratos
- Criação de contratos personalizados baseados em termos fornecidos
- Inclusão automática de cláusulas essenciais (rescisão, foro, etc.)
- Suporte a diversos tipos de contratos (locação, prestação de serviços, etc.)

### Análise Inteligente de Contratos
- Identificação das 3 cláusulas mais arriscadas
- Análise de complexidade e clareza
- Sugestões de refatoração para cláusulas problemáticas
- Processamento assíncrono para análises complexas

### Sistema de Usuários Multi-Role
- **ADMIN**: Acesso total ao sistema
- **LAWYER**: Pode analisar e gerar contratos
- **CLIENT**: Pode gerar contratos e visualizar seus documentos

### Gerenciamento de Contratos
- Listagem paginada de contratos
- Histórico de análises
- Status de processamento (PENDING, ANALYZED)

## Tecnologias Utilizadas

### Backend
- **Java 21** - Linguagem de programação
- **Spring Boot 3.5.8** - Framework principal
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **Spring AI** - Integração com Google Gemini
- **Spring AMQP** - Mensageria com RabbitMQ

### Inteligência Artificial
- **Ollama** - Plataforma local para execução de LLMs
- **GPT-OSS 20B** - Modelo de IA open-source para geração e análise (configurável)
- **Alternativas**: Llama 3.2, Mistral, Gemini via API

### Banco de Dados
- **Oracle Database** - Banco de dados relacional
- **HikariCP** - Pool de conexões

### Mensageria
- **RabbitMQ** - Fila de mensagens para processamento assíncrono

### Documentação
- **SpringDoc OpenAPI 3** - Documentação automática da API
- **Swagger UI** - Interface interativa para testes

### Ferramentas de Desenvolvimento
- **Maven** - Gerenciamento de dependências
- **Lombok** - Redução de boilerplate
- **Bean Validation** - Validação de dados

## Arquitetura

```
┌─────────────┐
│   Cliente   │
└──────┬──────┘
       │ HTTP/REST
       ▼
┌─────────────────────────────────┐
│     Spring Boot API             │
│  ┌──────────────────────────┐   │
│  │   Controllers            │   │
│  │   (REST Endpoints)       │   │
│  └───────────┬──────────────┘   │
│              │                  │
│  ┌───────────▼──────────────┐   │
│  │   Services               │   │
│  │   (Business Logic)       │   │
│  └───┬──────────────┬───────┘   │
│      │              │           │
│      ▼              ▼           │
│  ┌────────┐    ┌──────────┐    │
│  │  JPA   │    │ Spring   │    │
│  │  Repos │    │   AI     │    │
│  └────┬───┘    └─────┬────┘    │
│       │              │          │
└───────┼──────────────┼──────────┘
        │              │
        ▼              ▼
   ┌─────────┐   ┌──────────┐
   │ Oracle  │   │  Gemini  │
   │   DB    │   │   API    │
   └─────────┘   └──────────┘
        
        ┌──────────────┐
        │   RabbitMQ   │
        │  (Async)     │
        └──────────────┘
```

## Pré-requisitos

Antes de começar, você precisa ter instalado:

- **Java JDK 21** ou superior ([Download](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.8+** ([Download](https://maven.apache.org/download.cgi))
- **Oracle Database** (local ou cloud) ([Download](https://www.oracle.com/database/))
- **RabbitMQ** ([Download](https://www.rabbitmq.com/download.html))
- **Ollama** para execução local de LLMs ([Download](https://ollama.com))
- **Git** ([Download](https://git-scm.com/downloads))

### Verificar Instalação

```bash
java -version
mvn -version
rabbitmqctl status
```

## Instalação e Configuração

### 1. Clone o Repositório

```bash
git clone https://github.com/seu-usuario/contratAI-spring.git
cd contratAI-spring
```

### 2. Configure o Banco de Dados Oracle

Crie um usuário e schema no Oracle:

```sql
CREATE USER contratai IDENTIFIED BY sua_senha;
GRANT CONNECT, RESOURCE, DBA TO contratai;
GRANT UNLIMITED TABLESPACE TO contratai;
CREATE SEQUENCE USER_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE CONTRACT_SEQ START WITH 1 INCREMENT BY 1;
```

### 3. Configure o RabbitMQ

Inicie o RabbitMQ e crie as exchanges/queues necessárias:

```bash
rabbitmq-server
```

Acesse o Management Console em http://localhost:15672 (usuário padrão: guest / guest)

### 4. Configure o Ollama

Instale e inicie o Ollama:

```bash
# Windows
winget install Ollama.Ollama

# macOS
brew install ollama

# Linux
curl -fsSL https://ollama.com/install.sh | sh
```

Baixe o modelo de IA:

```bash
# Modelo padrão (GPT-OSS 20B)
ollama pull gpt-oss:20b

# Alternativas mais leves
ollama pull llama3.2:latest  # 3B params, ~2GB
ollama pull mistral:latest   # 7B params, ~4GB
```

Inicie o serviço:

```bash
ollama serve
```

Verifique se está rodando:

```bash
curl http://localhost:11434/api/tags
```

## Variáveis de Ambiente

Crie um arquivo `env.properties` na pasta `src/main/resources`:

```properties
SPRING_DATASOURCE_URL=jdbc:oracle:thin:@localhost:1521:XE
SPRING_DATASOURCE_USERNAME=contratai
SPRING_DATASOURCE_PASSWORD=sua_senha_oracle
```

**Nota**: O Ollama não requer API keys! Todas as configurações de IA são feitas no `application.properties`:

```properties
ollama.base-url=http://localhost:11434
ollama.model=gpt-oss:20b
```

### Exemplo de `env.properties` Completo

```properties
SPRING_DATASOURCE_URL=jdbc:oracle:thin:@localhost:1521:XE
SPRING_DATASOURCE_USERNAME=contratai
SPRING_DATASOURCE_PASSWORD=MinhaS3nh@Segura
```

### Modelos Ollama Disponíveis

| Modelo | Tamanho | RAM Necessária | Velocidade | Qualidade |
|--------|---------|----------------|------------|----------|
| `llama3.2:1b` | 1.3GB | 2GB | Muito rápida | Baixa |
| `llama3.2:latest` | 2GB | 4GB | Rápida | Média |
| `mistral:latest` | 4GB | 8GB | Moderada | Boa |
| `gpt-oss:20b` | 12GB | 16GB | Lenta | Alta |

Para trocar o modelo, edite `application.properties`:

```properties
ollama.model=llama3.2:latest
```

## Executando o Projeto

### Opção 1: Usando Maven

```bash
mvn clean install
mvn spring-boot:run
```

### Opção 2: Usando o JAR gerado

```bash
mvn clean package
java -jar target/contratAI-spring-0.0.1-SNAPSHOT.jar
```

### Opção 3: Usando IDE

1. Importe o projeto como Maven Project
2. Execute a classe `ContratAiSpringApplication.java`

### Verificar se está Funcionando

Acesse: `http://localhost:8080/swagger-ui/index.html`

Se a página do Swagger abrir, o sistema está funcionando corretamente.

## Documentação da API

A API possui documentação interativa completa gerada automaticamente com Swagger/OpenAPI.

### Acessar a Documentação

- **Swagger UI (Interface Interativa)**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

### Recursos da Documentação

- Descrição detalhada de todos os endpoints
- Exemplos de requisições e respostas
- Schemas de dados completos
- Testes interativos diretamente pelo navegador
- Informações sobre autenticação
- Códigos de resposta HTTP explicados

## Endpoints Disponíveis

### Usuários (`/api/v1/users`)

#### POST `/api/v1/users/register`
Registra um novo usuário no sistema.

**Acesso**: Público (não requer autenticação)

**Body**:
```json
{
  "name": "João Silva",
  "email": "joao.silva@example.com",
  "password": "senha123",
  "role": "CLIENT"
}
```

**Respostas**:
- `201 Created`: Usuário registrado com sucesso
- `400 Bad Request`: Dados inválidos ou e-mail já existe
- `500 Internal Server Error`: Erro no servidor

---

### Contratos (`/api/v1/contracts`)

#### GET `/api/v1/contracts/generate`
Gera um novo contrato usando IA.

**Acesso**: CLIENT, LAWYER, ADMIN

**Parâmetros**:
- `terms` (query, obrigatório): Termos do contrato

**Exemplo**:
```
GET /api/v1/contracts/generate?terms=locação residencial com duração de 12 meses
```

**Respostas**:
- `200 OK`: Contrato gerado (texto)
- `401 Unauthorized`: Não autenticado
- `403 Forbidden`: Sem permissão

---

#### POST `/api/v1/contracts/analyze`
Analisa um contrato existente (processamento assíncrono).

**Acesso**: LAWYER, ADMIN

**Body** (texto puro):
```
CONTRATO DE LOCAÇÃO RESIDENCIAL

Entre as partes:
LOCADOR: João da Silva, CPF...
LOCATÁRIO: Maria Santos, CPF...
...
```

**Respostas**:
- `202 Accepted`: Análise aceita para processamento
- `401 Unauthorized`: Não autenticado
- `403 Forbidden`: Apenas LAWYER e ADMIN podem analisar

---

#### GET `/api/v1/contracts`
Lista todos os contratos (paginado).

**Acesso**: CLIENT, LAWYER, ADMIN

**Parâmetros de Paginação**:
- `page` (opcional): Número da página (padrão: 0)
- `size` (opcional): Itens por página (padrão: 20)
- `sort` (opcional): Campo de ordenação (ex: `createdAt,desc`)

**Exemplo**:
```
GET /api/v1/contracts?page=0&size=10&sort=createdAt,desc
```

**Resposta** (`200 OK`):
```json
{
  "content": [
    {
      "id": 1,
      "title": "Contrato de Locação",
      "content": "...",
      "status": "ANALYZED",
      "createdAt": "2024-11-23T10:30:00",
      "owner": {
        "id": 5,
        "name": "João Silva",
        "email": "joao@example.com"
      }
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalPages": 5,
  "totalElements": 50
}
```

## Exemplos de Uso

### Registrar um Novo Usuário

```bash
curl -X POST http://localhost:8080/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Maria Santos",
    "email": "maria.santos@example.com",
    "password": "senha123",
    "role": "LAWYER"
  }'
```

**Resposta**:
```
Usuário registrado com sucesso. ID: 42
```

---

### Gerar um Contrato

```bash
curl -X GET "http://localhost:8080/api/v1/contracts/generate?terms=prestação de serviços de consultoria" \
  -H "Authorization: Basic bWFyaWEuc2FudG9zQGV4YW1wbGUuY29tOnNlbmhhMTIz"
```

**Resposta** (exemplo):
```
CONTRATO DE PRESTAÇÃO DE SERVIÇOS DE CONSULTORIA

Pelo presente instrumento particular...

CLÁUSULA PRIMEIRA - DO OBJETO
O presente contrato tem por objeto a prestação de serviços...

CLÁUSULA SEGUNDA - DO PRAZO
O prazo de vigência do presente contrato é de...

[... resto do contrato ...]
```

---

### Analisar um Contrato

```bash
curl -X POST http://localhost:8080/api/v1/contracts/analyze \
  -H "Content-Type: text/plain" \
  -H "Authorization: Basic bWFyaWEuc2FudG9zQGV4YW1wbGUuY29tOnNlbmhhMTIz" \
  -d "CONTRATO DE LOCAÇÃO

Entre as partes LOCADOR e LOCATÁRIO...
[conteúdo completo do contrato]"
```

**Resposta**:
```
Análise de contrato enviada para processamento assíncrono. 
O resultado será disponibilizado posteriormente.
```

---

### Listar Contratos com Paginação

```bash
curl -X GET "http://localhost:8080/api/v1/contracts?page=0&size=5&sort=createdAt,desc" \
  -H "Authorization: Basic bWFyaWEuc2FudG9zQGV4YW1wbGUuY29tOnNlbmhhMTIz"
```

---

### Usando Swagger UI

A forma mais fácil de testar a API é usando o Swagger UI:

1. Acesse: http://localhost:8080/swagger-ui/index.html
2. Clique no botão **"Authorize"** no topo
3. Digite suas credenciais (email:senha)
4. Clique em qualquer endpoint para testá-lo
5. Clique em "Try it out"
6. Preencha os parâmetros
7. Clique em "Execute"

## Autenticação e Autorização

### Tipo de Autenticação

O sistema usa **HTTP Basic Authentication**. Para cada requisição, você deve incluir o header:

```
Authorization: Basic <base64(email:senha)>
```

### Exemplo de Codificação

Para o usuário `maria@example.com` com senha `senha123`:

```bash
echo -n "maria@example.com:senha123" | base64
```

Use esse resultado no header:
```
Authorization: Basic bWFyaWFAZXhhbXBsZS5jb206c2VuaGExMjM=
```

### Roles e Permissões

| Role | Gerar Contrato | Analisar Contrato | Listar Contratos |
|------|----------------|-------------------|------------------|
| **CLIENT** | Sim | Não | Sim |
| **LAWYER** | Sim | Sim | Sim |
| **ADMIN** | Sim | Sim | Sim |

### Endpoints Públicos

Apenas o endpoint de registro não requer autenticação:
- `POST /api/v1/users/register`
- Documentação Swagger: `/swagger-ui/**`, `/v3/api-docs/**`

## Estrutura do Projeto

```
contratAI-spring/
│
├── src/
│   ├── main/
│   │   ├── java/com/fiap/contratAI_spring/
│   │   │   ├── config/
│   │   │   │   ├── OpenAPIConfig.java
│   │   │   │   ├── RabbitMQConfig.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── WebConfig.java
│   │   │   │
│   │   │   ├── controller/
│   │   │   │   ├── ContractController.java
│   │   │   │   └── UserController.java
│   │   │   │
│   │   │   ├── dto/
│   │   │   │   ├── UserRegistrationRequest.java
│   │   │   │   └── UserResponseDTO.java
│   │   │   │
│   │   │   ├── model/
│   │   │   │   ├── Contract.java
│   │   │   │   ├── Role.java
│   │   │   │   ├── User.java
│   │   │   │   └── enums/
│   │   │   │       ├── ContractStatus.java
│   │   │   │       └── RoleName.java
│   │   │   │
│   │   │   ├── queue/
│   │   │   │   └── ContractAnalysisListener.java
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   ├── ContractRepository.java
│   │   │   │   ├── RoleRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   │
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── ContractAIService.java
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   └── UserService.java
│   │   │   │
│   │   │   └── ContratAiSpringApplication.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── env.properties
│   │       └── i18n/
│   │           ├── message_en.properties
│   │           └── message_pt_BR.properties
│   │
│   └── test/
│       └── java/
│           └── ContratAiSpringApplicationTests.java
│
├── pom.xml
├── README.md
└── mvnw, mvnw.cmd
```

## Testando a Aplicação

### Testes Manuais via Swagger

1. Inicie a aplicação
2. Acesse http://localhost:8080/swagger-ui/index.html
3. Siga o fluxo:
   - Registre um usuário (role: LAWYER)
   - Autentique-se (botão Authorize)
   - Teste o endpoint de geração
   - Teste o endpoint de análise
   - Teste o endpoint de listagem

### Testes Automatizados

```bash
mvn test
mvn test jacoco:report
```

## Troubleshooting

### Problema: Erro de conexão com Oracle

**Solução**: Verifique se:
- O Oracle está rodando
- As credenciais em `env.properties` estão corretas
- O usuário tem as permissões necessárias

### Problema: Erro 401 Unauthorized

**Solução**: 
- Verifique se está usando o header de Authorization correto
- Confirme que o usuário está registrado no sistema
- Use o formato: `email:senha` (não username)

### Problema: Erro ao conectar com Gemini API

**Solução**:
- Verifique se a API Key está correta em `env.properties`
- Confirme que o projeto Google Cloud tem a API habilitada
- Verifique se há créditos disponíveis na conta

### Problema: RabbitMQ connection refused

**Solução**:
- Verifique se o RabbitMQ está rodando: `rabbitmqctl status`
- Confirme as configurações de host/porta no `application.properties`

## Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

### Padrões de Código

- Siga as convenções Java padrão
- Use Lombok para reduzir boilerplate
- Documente métodos públicos com JavaDoc
- Adicione testes para novas funcionalidades
- Mantenha a cobertura de testes acima de 70%

## Licença

Este projeto está licenciado sob a Apache License 2.0 - veja o arquivo LICENSE para detalhes.

## Equipe

Projeto desenvolvido como parte da Global Solution da FIAP.

**Desenvolvedores**:
- Seu Nome - [GitHub](https://github.com/seu-usuario)
- Colega 1 - [GitHub](https://github.com/colega1)
- Colega 2 - [GitHub](https://github.com/colega2)

## Contato

Para dúvidas ou sugestões:
- Email: contato@contratai.com
- Issues: [GitHub Issues](https://github.com/seu-usuario/contratAI-spring/issues)

## Agradecimentos

- FIAP pela proposta do desafio
- Google pelo Gemini AI
- Spring Team pela excelente documentação
- Comunidade Open Source

---

Feito pela equipe ContratAI
