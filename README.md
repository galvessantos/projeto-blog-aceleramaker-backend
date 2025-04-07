# 📝 Projeto Blog Pessoal - Acelera Maker

API RESTful desenvolvida com Java e Spring Boot para gerenciamento de usuários, temas e postagens. Projeto criado durante o **Bootcamp Acelera Maker** da empresa **Montreal**.

## 🛠️ Tecnologias utilizadas

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- MySQL
- Hibernate
- Maven
- Swagger (Springdoc OpenAPI)
- SonarQube
- JUnit 5 & Mockito
- Bean Validation

---

## 🔐 Segurança

- Autenticação via JWT
- Senhas criptografadas com BCrypt
- Acesso controlado por roles
- Restrições de acesso por usuário autenticado (ex: só pode alterar ou excluir sua própria conta)
- Validações avançadas nos DTOs
- Tokens sensíveis protegidos (ex: SonarQube Token via variável de ambiente)

---

## 🧪 Testes

O projeto possui cobertura com testes:

- ✅ Testes Unitários
- ✅ Testes de Integração

Cobrem:
- Controllers (`Auth`, `Usuario`, `Postagem`, `Tema`)
- Services
- Regras de segurança

---

## 📚 Endpoints & Documentação

A documentação da API está disponível via Swagger:

🔗 [`http://localhost:8080/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html)

![Image](https://github.com/user-attachments/assets/9dbb77da-9400-480a-99ba-a34a07d61742)

Principais funcionalidades:

- Criar, listar, atualizar e deletar usuários
- Login com geração de token JWT
- Criar e gerenciar postagens e temas
- Filtros por tema e título
- Validações e mensagens amigáveis em erros

---

## 🗃️ Estrutura do banco de dados

### Entidades principais:

- `Usuario`
- `Postagem`
- `Tema`

### Relacionamentos:

- Um `Usuario` pode ter várias `Postagem`s  
- Uma `Postagem` pertence a um `Tema`  
- Um `Tema` pode ter várias `Postagem`s

---

## ⚙️ Como rodar o projeto localmente

```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/projeto01-blog-aceleramaker.git

# 2. Acesse a pasta do projeto
cd projeto01-blog-aceleramaker

# 3. Configure o banco de dados MySQL
# Crie um schema chamado 'blog' e atualize o application.properties

# 4. Configure a variável de ambiente para o token do SonarQube (opcional)
export SONAR_TOKEN=seu_token

# 5. Rode a aplicação
./mvnw spring-boot:run
```

---

## 🔎 Qualidade de código

O projeto utiliza **SonarQube** para análise estática e verificação de boas práticas de código. O token do SonarQube é mantido seguro fora do código-fonte.

![Image](https://github.com/user-attachments/assets/fd4ff623-88b8-4d3a-8de7-15c69158b223)
---

## 👤 Autor

Desenvolvido por **Gabriel Alves**  
Bootcamp **Acelera Maker - Montreal**

---



