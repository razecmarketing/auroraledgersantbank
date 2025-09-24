## DECISIONS.md (Engineering Trade-offs)

Foco: registrar escolhas com contexto sucinto. Cada entrada: Problema | Alternativas | Decisão | Trade‑offs | Próxima Reavaliação.

### 1. Linguagem & Runtime
Problema: Plataforma estável para protótipo de ledger com APIs REST.
Alternativas: Java 17, Kotlin, Go, Node.js
Decisão: Java 17 + Spring Boot 3.5.x
Trade-offs: + Ecossistema maduro, libs de segurança; - Verbosidade maior que Go/Kotlin.
Reavaliação: Quando latência p95 de endpoints críticos > 150ms sob 200 req/s.

### 2. Framework Web
Alternativas: Spring Boot, Micronaut, Quarkus
Decisão: Spring Boot (equilíbrio produtividade / integração libs)
Trade-offs: Startup mais lenta que Quarkus; memória maior que Micronaut.
Reavaliação: Se cold start/server footprint virar gargalo.

### 3. Persistência Primária
Alternativas: PostgreSQL, MySQL, MongoDB
Decisão: PostgreSQL (ACID forte + extensões)
Trade-offs: + Consistência; - Operações de escrita em alto volume exigirão tunning.
Reavaliação: > 5k TPS sustentados necessários.

### 4. Banco Em Memória / Cache
Alternativas: Redis, Caffeine local, Hazelcast
Decisão: Caffeine local (atual) + Planejado Redis para idempotência e cache de modelo de leitura.
Trade-offs: Simples porém não distribui; requer futura migração para cross-instance.
Reavaliação: Ao escalar para múltiplas instâncias.

### 5. Mensageria / Eventos
Alternativas: Kafka, Redis Streams, RabbitMQ
Decisão: Kafka (planejado) para partição e reprocessamento de eventos.
Trade-offs: Operação mais complexa; só será adotado quando existir evento real a publicar.
Reavaliação: Antes da primeira feature assíncrona.

### 6. Autenticação
Alternativas: JWT HS512, RSA, Session Store
Decisão: JWT HS512 (demo secret) – simplicidade inicial.
Trade-offs: Rotação manual e risco se segredo vaza; migração futura para chave assimétrica.
Reavaliação: Antes de expor publicamente fora de ambiente controlado.

### 7. Migração para Event Sourcing
Estado: Aspiracional. Somente modelo relacional direto hoje.
Condição de Adoção: Quando necessidade de reconstrução de projeções surgir / auditoria forte.
Risco: Complexidade de consistência eventual sem necessidade real.

### 8. Observabilidade
Decisão: Prometheus + Grafana (compose) apenas; sem tracing completo ainda.
Próximo Passo: Definir métricas de latência e erro; adicionar trace-id / idem-key em log.

### 9. Cobertura de Testes
Decisão: Sem alvo percentual fixo. Foco em invariáveis (saldo ≥ 0, idempotência, reconciliação).
Motivo: Evitar gamificação de cobertura.

### 10. Política de Branch / CI
Decisão: CI simples (build + test + jacoco artifact). Badge só após métricas reais.

---
Formato Futuro para novas decisões:

```
#### N. Título Curto
Problema:
Alternativas:
Decisão:
Trade-offs (+ / -):
Evidência Inicial:
Reavaliação Em:
```
