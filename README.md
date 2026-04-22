# 🧵 Conexão dos Retalhos

> Unindo pedaços e criando histórias. Uma plataforma de e-commerce sustentável para produtos feitos de retalhos, conectando criatividade ao cuidado com o meio ambiente.

---

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Funcionalidades](#funcionalidades)
- [Segurança e Autenticação](#segurança-e-autenticação)
- [Pré-requisitos](#pré-requisitos)
- [Configuração e Instalação](#configuração-e-instalação)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Executando o Projeto](#executando-o-projeto)
- [Estrutura do Projeto](#estrutura-do-projeto)

---

## 📖 Sobre o Projeto

**Conexão dos Retalhos** Desenvolvido para atender o nicho de artesanato exclusivo, o Conexão dos Retalhos é um e-commerce full-stack construído sobre o ecossistema **Spring Boot**. A plataforma gerencia produtos com estoque único, assegurando que cada item seja vendido apenas uma vez. Tecnicamente, o projeto destaca-se por seu sistema de controle de acesso e autenticação, painel administrativo para gestão de inventário e notificações automáticas via e-mail. Com o core do backend consolidado, as próximas etapas de desenvolvimento incluem a integração de APIs de pagamento e serviços de frete para viabilizar a operação logística.

O projeto foi inicializado utilizando o **[Spring Initializr](https://start.spring.io/)**, ferramenta oficial da Spring para geração de projetos com as dependências configuradas.

---

## 🚀 Tecnologias Utilizadas

| Camada | Tecnologia |
|--------|-----------|
| **Backend** | Java 17, Spring Boot 3.3.11 |
| **Persistência** | Spring Data JPA, Hibernate, PostgreSQL |
| **Segurança** | Spring Security |
| **Frontend** | Thymeleaf, Bootstrap 5.3, Font Awesome 6 |
| **E-mail** | Spring Mail (SMTP Gmail) |
| **Build** | Maven |
| **Utilitários** | Lombok, Apache Commons IO |
| **Dev** | Spring Boot DevTools |
| **Inicialização** | [Spring Initializr](https://start.spring.io/) |

---

## ✅ Funcionalidades

### 👤 Usuário (`ROLE_USER`)
- Cadastro e login com e-mail e senha
- Navegação e busca de produtos com paginação
- Carrinho de compras com contador dinâmico
- Realização e acompanhamento de pedidos
- Visualização e edição de perfil
- Troca de senha

### 🛠️ Administrador (`ROLE_ADMIN`)
- Painel administrativo exclusivo
- Gerenciamento de produtos (CRUD completo)
  - Upload de até **3 imagens por produto**
  - Busca e paginação de produtos
  - Ativar/inativar produtos
- Gerenciamento de pedidos
  - Listagem paginada e busca por ID de pedido
  - Atualização de status do pedido
  - **Envio automático de e-mail** ao cliente a cada mudança de status
- Gerenciamento de usuários
  - Listagem separada de `ROLE_USER` e `ROLE_ADMIN`
  - Ativar/desativar contas de usuário
- Cadastro de novos administradores
- Visualização e edição de perfil próprio

### 📧 E-mail
- Envio de e-mails transacionais via Gmail SMTP (notificações de pedido, confirmações, etc.)

---

## 🔐 Segurança e Autenticação

A segurança é gerenciada pelo **Spring Security** com implementações customizadas para atender às regras de negócio da aplicação.

### Autenticação com `UserDetailsService` customizado
- Login realizado via **e-mail** (`loginEmail`) em vez do username padrão
- Implementação de `UserDetailsService` (`UserDetailsServiceImpl`) que busca o usuário no banco e retorna um `CustomUser` (implementação de `UserDetails`)

### Controle de Acesso por Roles
- `/admin/**` → acessível apenas por `ROLE_ADMIN`
- `/user/**` → acessível apenas por `ROLE_USER`
- `/**` → público (produtos, home, login, cadastro)

### Handler de Login com Sucesso (`AuthSuccessHandlerImpl`)
- Redireciona automaticamente após o login com base na role do usuário:
  - `ROLE_ADMIN` → `/admin/`
  - `ROLE_USER` → `/` (home)

### Handler de Falha no Login (`AuthFailureHandlerImpl`)
O sistema possui proteção contra tentativas excessivas de login:

| Situação | Comportamento |
|----------|---------------|
| E-mail não cadastrado | Mensagem genérica de credenciais inválidas |
| Senha incorreta (tentativas < limite) | Incrementa contador de tentativas falhas |
| Senha incorreta (limite atingido) | **Bloqueia a conta** automaticamente |
| Conta bloqueada (tempo ainda vigente) | Informa que a conta está bloqueada |
| Conta bloqueada (tempo expirado) | **Desbloqueia automaticamente** a conta |
| Conta inativa | Orienta o usuário a verificar o e-mail |

### Gerenciamento de Sessão
- O Spring Security gerencia as sessões de usuário de forma automática
- Dados do usuário autenticado são disponibilizados nos templates via `Principal` e `Model`
- O logout invalida a sessão e está disponível para todos os perfis

### Configurações de Segurança
- Senhas armazenadas com hash **BCrypt**
- CSRF desativado (adequado para a arquitetura atual)
- `DaoAuthenticationProvider` configurado com `UserDetailsService` + `PasswordEncoder`

---

## 🛠️ Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- [Java 17+](https://adoptium.net/)
- [Maven 3.8+](https://maven.apache.org/)
- [PostgreSQL 14+](https://www.postgresql.org/)
- Uma conta Gmail com [Senha de App](https://support.google.com/accounts/answer/185833) configurada (para envio de e-mails)

---

## ⚙️ Configuração e Instalação

### 1. Clone o repositório

```bash
git clone https://github.com/PedroAntonioRodrigues1/ecommerce-java-springboot.git
cd vendas
```

### 2. Crie o banco de dados PostgreSQL

```sql
CREATE DATABASE "loja-retalhos";
```

### 3. Crie a pasta para armazenamento de imagens

As imagens dos produtos são salvas em uma pasta externa ao projeto. Por padrão, o caminho configurado é:

```
C:\conexao-retalhos\imagens\
```

> Você pode alterar esse caminho em `WebConfig.java`, na propriedade `addResourceLocations`.

### 4. Configure as variáveis de ambiente

Defina as variáveis conforme a seção abaixo.

### 5. Compile e instale as dependências

```bash
mvn clean install
```

---

## 🔑 Variáveis de Ambiente

| Variável | Descrição | Valor padrão |
|----------|-----------|--------------|
| `DB_URL` | URL de conexão com o PostgreSQL | `jdbc:postgresql://localhost:5432/loja-retalhos` |
| `DB_USER` | Usuário do banco de dados | `postgres` |
| `DB_PASSWORD` | Senha do banco de dados | — |
| `MAIL_USER` | E-mail Gmail para envio | — |
| `MAIL_PASSWORD` | Senha de App do Gmail | — |

> ⚠️ **Nunca** commite senhas ou credenciais no repositório. Use variáveis de ambiente ou um arquivo `.env` excluído via `.gitignore`.

### Exemplo de `.env`

```env
DB_URL=jdbc:postgresql://localhost:5432/loja-retalhos
DB_USER=postgres
DB_PASSWORD=sua_senha_aqui
MAIL_USER=seuemail@gmail.com
MAIL_PASSWORD=sua_senha_de_app_aqui
```

---

## ▶️ Executando o Projeto

### Com Maven

```bash
mvn spring-boot:run
```

### Com JAR gerado

```bash
mvn package
java -jar target/vendas-0.0.1-SNAPSHOT.jar
```

Acesse a aplicação em: **http://localhost:8080**

---

## 📁 Estrutura do Projeto

```
vendas/
├── src/
│   ├── main/
│   │   ├── java/com/retalho/vendas/
│   │   │   ├── config/               # Segurança (Spring Security)
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── CustomUser.java
│   │   │   │   ├── UserDetailsServiceImpl.java
│   │   │   │   ├── AuthSucessHandlerImpl.java
│   │   │   │   ├── AuthFailureHandlerImpl.java
│   │   │   │   └── WebConfig.java
│   │   │   ├── controller/           # Controllers MVC
│   │   │   │   ├── AdminController.java
│   │   │   │   ├── UserController.java
│   │   │   │   └── HomeController.java
│   │   │   ├── model/                # Entidades JPA
│   │   │   ├── repository/           # Repositórios Spring Data
│   │   │   ├── service/              # Interfaces de serviço
│   │   │   │   └── impl/             # Implementações
│   │   │   └── util/                 # Utilitários (CommonUtil, OrderStatus)
│   │   └── resources/
│   │       ├── templates/            # Templates Thymeleaf
│   │       │   ├── admin/            # Páginas do painel admin
│   │       │   ├── user/             # Páginas do usuário
│   │       │   └── layout/           # Layout base (navbar, footer)
│   │       ├── static/               # CSS, JS, imagens estáticas
│   │       ├── messages.properties   # Mensagens i18n (pt_BR)
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

---

## 📄 Licença

Este projeto está sob desenvolvimento. Consulte o responsável pelo projeto para informações sobre licenciamento.

---

<p align="center">Feito com 🧡 e retalhos</p>
