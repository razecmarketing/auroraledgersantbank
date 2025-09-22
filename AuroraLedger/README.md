# Aurora Ledger

Aplica��o demonstrativa de transa��es banc�rias constru�da em Java 17 / Spring Boot 3 com foco em padr�es Santander: CQRS, seguran�a JWT, persist�ncia consistente e proje��es em cache para consultas em tempo real.

## Vis�o Geral
 **Cadastro** com valida��o de CPF e persist�ncia em PostgreSQL.
 **Autentica��o JWT** para habilitar dep�sitos, pagamentos e consultas.
 **Dom�nio financeiro** com doubleentry, negativa��o com juros de 1,02% e hist�rico audit�vel.
 **CQRS completo**: write model relacional, proje��es sincronizadas em Redis + MongoDB.

## Stack T�cnica
| Camada | Tecnologia |
|  |  |
| Linguagem | Java 17 |
| Framework | Spring Boot 3 (Web, Security, Data JPA) |
| Persist�ncia (write) | PostgreSQL / H2 (dev) |
| Proje��es (read) | Redis + MongoDB |
| Documenta��o | springdocopenapi |
| Testes | JUnit 5, Mockito |

## Como Executar
1. **Requisitos**: Java 17+, Maven, Docker Desktop.
2. **Infra local**: `dockercompose up d` (Postgres, Redis, Mongo).
3. **Build**: `mvn clean package`.
4. **Aplica��o**: `mvn springboot:run`.
5. **Swagger**: http://localhost:8080/swaggerui

## Testes
 `mvn clean test`
 Relat�rio consolidado em `TEST_ASSURANCE_REPORT.md` (100% dos requisitos do contexto cobertos entre testes autom�ticos e manuais).

## Pr�ximos Passos
1. Pipeline CI/CD com Testcontainers + Sonar.
2. M�tricas (OpenTelemetry) e alertas banc�rios.
3. Amplia��o do dom�nio para PIX, boletos e regras PJ.

> Desenvolvido por Cezi Cola � Senior Software Engineer (Bio Code Technology).

