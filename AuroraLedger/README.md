# Aurora Ledger

Aplicação demonstrativa de transações bancárias construída em Java 17 / Spring Boot 3 com foco em padrões Santander: CQRS, segurança JWT, persistência consistente e projeções em cache para consultas em tempo real.

## Visão Geral
- **Cadastro** com validação de CPF e persistência em PostgreSQL.
- **Autenticação JWT** para habilitar depósitos, pagamentos e consultas.
- **Domínio financeiro** com double-entry, negativação com juros de 1,02% e histórico auditável.
- **CQRS completo**: write model relacional, projeções sincronizadas em Redis + MongoDB.

## Stack Técnica
| Camada | Tecnologia |
| --- | --- |
| Linguagem | Java 17 |
| Framework | Spring Boot 3 (Web, Security, Data JPA) |
| Persistência (write) | PostgreSQL / H2 (dev) |
| Projeções (read) | Redis + MongoDB |
| Documentação | springdoc-openapi |
| Testes | JUnit 5, Mockito |

## Como Executar
1. **Requisitos**: Java 17+, Maven, Docker Desktop.
2. **Infra local**: `docker-compose up -d` (Postgres, Redis, Mongo).
3. **Build**: `mvn clean package`.
4. **Aplicação**: `mvn spring-boot:run`.
5. **Swagger**: http://localhost:8080/swagger-ui

## Testes
- `mvn clean test`
- Relatório consolidado em `TEST_ASSURANCE_REPORT.md` (100% dos requisitos do contexto cobertos entre testes automáticos e manuais).

## Próximos Passos
1. Pipeline CI/CD com Testcontainers + Sonar.
2. Métricas (OpenTelemetry) e alertas bancários.
3. Ampliação do domínio para PIX, boletos e regras PJ.

> Desenvolvido pelo time Aurora Ledger Engineering Team para demonstrar o que um engenheiro de software pode entregar em um ambiente bancário de alta exigência.
