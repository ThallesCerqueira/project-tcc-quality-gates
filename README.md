# CRUD de Produtos — Projeto Base para CI/CD e Quality Gates

Aplicação fullstack de gerenciamento de produtos utilizada como objeto de estudo para automação de testes de API, integração contínua e implementação de Quality Gates em pipelines CI/CD.

## Stack

| Camada | Tecnologia |
|---|---|
| Backend | Node.js + Express |
| Banco | PostgreSQL 16 |
| Frontend | React + Vite |
| Testes de API | Java + RestAssured + JUnit 5 |
| Build dos testes | Maven |
| CI/CD | GitHub Actions |
| Container | Docker + Docker Compose |

## Estrutura do projeto

```text
produto-crud/

├── backend/
│   ├── src/
│   │   ├── app.js
│   │   ├── server.js
│   │   ├── controllers/
│   │   ├── routes/
│   │   └── db/
│   │       ├── pool.js
│   │       └── init.sql
│   ├── Dockerfile
│   └── package.json
│
├── frontend/
│   ├── src/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
│
├── api-tests/
│   ├── pom.xml
│   └── src/
│       └── test/
│           └── java/
│
├── .github/
│   └── workflows/
│       └── ci.yml
│
├── docker-compose.yml
└── README.md