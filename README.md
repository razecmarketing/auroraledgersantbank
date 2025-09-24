# Aurora Ledger (Backend Prototype)

Concise description: Java / Spring Boot prototype exploring basic account operations (register, authenticate, deposit, pay, balance) and early CQRS-style separation. Prior broad claims (enterprise-grade, production ready, multi-compliance) were intentionally removed to increase credibility.

## Current Implemented Scope
- Registration + JWT authentication (HS512 demo secret — rotate in real environments)
- Deposit & payment endpoints with simple overdraft interest example (illustrative only)
- Balance + basic transaction history persistence (not immutable yet)
- Docker compose services (Redis, MongoDB, Kafka, Zookeeper, Prometheus, Grafana) present but only partially integrated
- No formal regulatory compliance or SLA claims

## Planned (Not Implemented Yet)
(Items remain until código + evidência exist.)
- Idempotent transfer endpoint using `Idempotency-Key` header
- Event replay / read model rebuild command
- Hash‑chained append-only audit log + verification script
- Load testing harness + execuções registradas em `docs/perf/`
- Coverage badge público (artifact interno já gerado em CI)

## Technology (Used / Scaffolding)
| Component | Status | Notes |
|-----------|--------|-------|
| Java 17 + Spring Boot 3.5.x | In use | Core framework |
| Spring Security + JWT (HS512) | In use | Demo secret; rotation TBD |
| H2 (dev) / PostgreSQL profile | In use / profile | Persistence |
| MongoDB | Scaffolding | Intended future event log store |
| Redis | Planned | Idempotency keys / read model cache |
| Kafka + Zookeeper | Planned | Async processing / events |
| Prometheus + Grafana | Infra running | App metrics minimal |

## Authenticity Artifacts
Present in repo (evolução contínua):
- [`DECISIONS.md`](./DECISIONS.md)
- [`SCOPE_LIMITATIONS.md`](./SCOPE_LIMITATIONS.md)
- [`RATIONALE_LOG.md`](./RATIONALE_LOG.md)
- [`THREAT_MODEL.md`](./THREAT_MODEL.md)
- [`PERFORMANCE_NOTES.md`](./PERFORMANCE_NOTES.md)
- [`CHECKLIST_CREDIBILITY.md`](./CHECKLIST_CREDIBILITY.md)
- [`GIT_HELP.md`](./GIT_HELP.md)

## Security (Prototype)
- JWT HS512 tokens (24h). Secret rotation & key management not implemented.
- BCrypt password hashing (verify work factor in configuration).
- Input validation minimal; hardening pending.
- No KYC/AML, PCI, PSD2, LGPD claims.

## Data & Audit
- Transaction history stored; not yet immutable or hash-linked.
- Planned: append-only audit stream with previous-hash linkage.

## Observability
- Prometheus & Grafana containers available.
- Needs: structured logs w/ correlation & idempotency IDs; latency/error metrics; optional tracing hooks.

## Run (Dev)
```bash
# Optional infra
docker compose up -d

# Application
cd AuroraLedger
mvn spring-boot:run
```
Endpoints: `http://localhost:8080`  |  Swagger UI: `/swagger-ui`  |  Health: `/actuator/health`

## Tests
```bash
mvn clean test
mvn clean test jacoco:report
```
Coverage badge will be added only after CI publishes metrics.

## Commit Style
Single maintainer: CEZI COLA Senior Software Engineer
Format: `feat|fix|chore(scope): factual change` (avoid adjectives).

## Removed Prior Content / Hygiene
- Marketing phrases (enterprise‑grade, production ready)
- Unsupported compliance acronyms (PCI, Basel III, SOX, MiFID, etc.)
- Absolute metrics (100% coverage, 58/58 passed) without dynamic artifact
- Multi-team role listings & inspirational historical prose

## Future Notes
As new capabilities land, this file should grow only with verifiable evidence (tests, scripts, metrics, configs). Unsupported or aspirational claims remain in Planned until proven.

---
README version: 2025-09-23
