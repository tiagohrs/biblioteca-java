# Sistema de Biblioteca

API REST em Spring Boot para cadastro de leitores e livros e controle de empréstimos, devoluções, renovações e atrasos.

## Requisitos

- Java 21
- MySQL em execução, com o schema `biblioteca` criado

## Configuração

A aplicação usa `jdbc:mysql://localhost:3306/biblioteca` e cria/atualiza as tabelas com Hibernate (`spring.jpa.hibernate.ddl-auto=update`). A senha do banco não fica no repositório: defina a variável de ambiente `DB_PASSWORD` antes de iniciar. O usuário padrão é `root`; para alterar, defina `DB_USERNAME`.

No PowerShell:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "sua-senha"
```

## Executar

Na raiz do projeto:

```powershell
./mvnw spring-boot:run
```

A API fica disponível em `http://localhost:8080`. As requisições de exemplo estão na pasta `postman`.

## Rotas principais

- `/leitores` — cadastro, consulta, atualização e remoção de leitores
- `/livros` — cadastro, consulta, atualização e remoção de livros
- `/emprestimos` — cadastro e consulta de empréstimos
- `/emprestimos/ativos`, `/emprestimos/atrasados` — consultas de empréstimos ativos e atrasados
- `/emprestimos/leitor/{leitorId}` — empréstimos de um leitor
- `/emprestimos/livro/{livroId}` — empréstimos de um livro
- `/emprestimos/{id}/devolucao` — registrar devolução (PATCH)
- `/emprestimos/{id}/renovacao` — renovar empréstimo (PATCH)
