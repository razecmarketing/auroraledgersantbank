## THREAT_MODEL.md (Esqueleto STRIDE Focado)

Escopo: Protótipo monolito ledger com JWT HS512, PostgreSQL, intenção futura de eventos.
Metodologia: STRIDE simplificado em ativos críticos.

| Ativo | Ameaça (STRIDE) | Vetor Provável | Controle Atual | Lacuna / Próximo Passo |
|-------|-----------------|----------------|----------------|------------------------|
| Token JWT | Tampering / Replay | Segredo exposto / longa validade | Segredo único env; expiração 24h | Rotação + key id + redução tempo expiração |
| Credenciais Usuário | Disclosure | Log inseguro / transporte sem TLS | BCrypt + (avisos) | Forçar TLS, política de senha forte |
| Saldo Conta | Integrity | Race condition / atualização concorrente | Operações sincronizadas simples | Introduzir optimistic locking / versão |
| Log Transações | Repudiation | Alteração direta no DB | Histórico simples sem hash | Adicionar coluna prev_hash + verificação diária |
| Endpoints Autenticados | DoS | Flood requests | Sem rate limit | Implementar bucket token / throttling |

Riscos fora de escopo atual: multi-tenant isolation, análise de fraude em tempo real.

Revisar tabela a cada incremento que introduza novo ativo ou controle.
