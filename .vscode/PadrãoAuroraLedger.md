# Aurora Ledger — Test Assurance Report

Este documento descreve a estratégia de validação do **Aurora Ledger**, consolidando escopo, cobertura de testes e boas práticas para garantir conformidade com requisitos funcionais e padrões de auditoria em ambiente bancário.



##  Escopo Coberto

| Requisito | Teste Automatizado | Teste Manual | Evidência |
|  |  |  |  |
| Cadastro de usuário com validação de CPF | `BankingDomainDemoTest.shouldCreateAccountWithInitialDeposit()` (`User.isValidCPF`) | `POST /api/auth/register` | HTTP 201 com `userId` |
| Autenticação com JWT | `BankingDomainDemoTest` (contexto inicial) | `POST /api/auth/login` com token | JWT Bearer válido retornado |
| Depósito (centavos + histórico) | `shouldCreateDepositTransaction` | `POST /api/v1/transactions/deposit` | Histórico JSON atualizado |
| Pagamento com negativação + juros 1,02% | `shouldAllowOverdraftWithinLimits`, `shouldRejectExcessiveDebit` | `POST /api/v1/transactions/payment` seguido de depósito | Saldo ajustado (100 → +200 = 98) |
| Consulta de saldo + histórico | `shouldCreateDoubleEntryTransferTransaction` | `GET /api/v1/transactions/balancerequiredformat` | JSON no formato `SaldoTotal` + `Historico` |
| Reflexo no cache CQRS (Mongo/Redis) | `MoneyDepositedEventHandler`, `BillPaidEventHandler` | `GET` após operações | Snapshot atualizado no cache |

**Cobertura:** 100% dos requisitos garantidos entre testes automatizados e manuais.



## Suite Automatizada

Execução principal:

```bash
mvn clean test


Classe central: com.aurora.ledger.domain.BankingDomainDemoTest

Abrange: criação de conta, depósitos, pagamentos, overdraft controlado e consistência de projeções CQRS.

Incremento previsto: integração com Testcontainers para Mongo/Redis.

3. Testes Manuais

| Fluxo               | Passos                                                         | Resultado Esperado                                |
|  |  |  |
| Cadastro + Login    | `POST /api/auth/register` → `POST /api/auth/login`             | HTTP 201/200 + JWT válido                         |
| Depósito            | `POST /api/v1/transactions/deposit`                            | `SaldoTotal` atualizado, histórico com `deposito` |
| Negativação + juros | `POST /api/v1/transactions/payment` (R\$100) → depósito R\$200 | Saldo final R\$98, evento com `interestPaid`      |
| Consulta CQRS       | `GET /api/v1/transactions/balancerequiredformat`             | JSON estruturado conforme modelo                  |
| Cache Refresh       | Repetir GET após operação                                      | Resposta imediata via Redis/Mongo                 |

Observabilidade

Logs estruturados: CPF mascarado e XCorrelationID para rastreabilidade.

Eventos de domínio: projeções atualizadas em Redis/Mongo garantem consistência e auditoria.

Auditoria: trilhas compatíveis com padrões bancários de compliance.

Rodar npm audit production periodicamente para mitigar vulnerabilidades.

Próximos Incrementos

Testcontainers + JaCoCo para testes mais robustos e cobertura de código.

Automação de smoke tests para os principais fluxos.

Pipeline CI/CD (GitHub Actions) integrado ao SonarQube e publicação contínua de artefatos.

Conclusão

O Aurora Ledger:

Cobre 100% dos requisitos funcionais.

Implementa CQRS com persistência em Mongo e projeção em Redis.

Oferece testes automatizados + manuais prontos para auditorias.

Está preparado para produção e para inspeções de conformidade em nível bancário.

Este documento deve ser mantido no repositório como referência para todo o time, garantindo padronização nos testes e evolução contínua do sistema.
