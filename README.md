# 🧵 Conexão dos Retalhos

> Unindo pedaços e criando histórias. Uma plataforma de e-commerce sustentável para produtos feitos de retalhos, conectando criatividade ao cuidado com o meio ambiente.

---

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Funcionalidades](#funcionalidades)
- [Pré-requisitos](#pré-requisitos)
- [Configuração e Instalação](#configuração-e-instalação)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Executando o Projeto](#executando-o-projeto)
- [Estrutura do Projeto](#estrutura-do-projeto)

---

## 📖 Sobre o Projeto

**Conexão dos Retalhos** é uma aplicação web de e-commerce desenvolvida com **Spring Boot**, voltada para a venda de produtos artesanais e sustentáveis feitos de retalhos de tecido. A plataforma oferece uma experiência completa de compra, com área de administração, gestão de pedidos e envio de e-mails transacionais.

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

---

## ✅ Funcionalidades

### 👤 Usuário (`ROLE_USER`)
- Cadastro e login de usuários
- Navegação e busca de produtos
- Carrinho de compras
- Realização e acompanhamento de pedidos
- Visualização de perfil

### 🛠️ Administrador (`ROLE_ADMIN`)
- Painel administrativo
- Gerenciamento de produtos (CRUD)
- Visualização e gestão de pedidos
- Gerenciamento de usuários

### 📧 E-mail
- Envio de e-mails transacionais via Gmail SMTP (confirmações de pedido, cadastro, etc.)

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
git clone https://github.com/seu-usuario/vendas.git
cd vendas
```

### 2. Crie o banco de dados PostgreSQL

```sql
CREATE DATABASE "loja-retalhos";
```

### 3. Configure as variáveis de ambiente

Defina as variáveis de ambiente conforme descrito na seção abaixo. Em desenvolvimento, você pode criar um arquivo `.env` ou configurar diretamente no `application.properties`.

### 4. Instale as dependências e compile

```bash
mvn clean install
```

---

## 🔐 Variáveis de Ambiente

Configure as seguintes variáveis de ambiente antes de executar a aplicação:

| Variável | Descrição | Exemplo |
|----------|-----------|---------|
| `DB_URL` | URL de conexão com o PostgreSQL | `jdbc:postgresql://localhost:5432/loja-retalhos` |
| `DB_USER` | Usuário do banco de dados | `postgres` |
| `DB_PASSWORD` | Senha do banco de dados | `sua_senha` |
| `MAIL_USER` | E-mail Gmail para envio | `seuemail@gmail.com` |
| `MAIL_PASSWORD` | Senha de App do Gmail | `xxxx xxxx xxxx xxxx` |

> ⚠️ **Nunca** commite senhas ou credenciais no repositório. Use variáveis de ambiente ou um arquivo `.env` excluído do `.gitignore`.

### Exemplo de `.env` (para desenvolvimento local)

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
│   │   │   ├── controller/       # Controllers MVC
│   │   │   ├── model/            # Entidades JPA
│   │   │   ├── repository/       # Repositórios Spring Data
│   │   │   ├── service/          # Lógica de negócio
│   │   │   ├── security/         # Configurações Spring Security
│   │   │   └── VendasApplication.java
│   │   └── resources/
│   │       ├── templates/        # Templates Thymeleaf
│   │       │   └── layout/       # Layout base (navbar, footer)
│   │       ├── static/           # CSS, JS, imagens
│   │       ├── messages.properties
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
