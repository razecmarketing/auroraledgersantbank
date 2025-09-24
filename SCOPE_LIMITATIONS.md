## SCOPE_LIMITATIONS.md (Limitações & Não Metas)

Objetivo: Tornar explícito o que NÃO está incluído para evitar sobreinterpretação.

### Não Implementado / Fora de Escopo Atual
- Conformidade formal (PCI, PSD2, AML, LGPD avançada) – sem artefatos de auditoria.
- Processamento em alta escala (cluster horizontal, sharding, multi-região).
- Alta disponibilidade (sem failover automatizado / sem readiness liveness detalhado).
- Event Sourcing completo (apenas CRUD relacional hoje).
- Outbox / Inbox confiável (interfaces esboçadas, sem persistência de outbox real).
- Rotação de chaves JWT ou chave assimétrica.
- Encriptação de campos sensíveis em repouso.
- Mecanismo de limites de taxa (rate limiting) / proteção anti-brute force.
- Mecanismo de reconciliação financeiro formal ou dupla contabilidade (double-entry ledger).
- Console de administração / UI.
- SLA de latência ou disponibilidade comprometidos publicamente.
- Mecanismo antifraude / machine learning.

### Assunções Temporárias
- Segredo JWT fornecido por variável de ambiente durante desenvolvimento.
- Uma única instância da aplicação (sem coordenação distribuída de locks).
- Transações financeiras simples, sem múltipla moeda ou FX.

### Condições que Promovem Revisão
- Necessidade de auditoria externa.
- Escala de tráfego sustentado > 1000 req/s.
- Requisitos legais/regulatórios formais.
- Exposição pública a clientes externos.

---
Adicionar novas linhas sempre que remover funcionalidade anteriormente planejada ou adiar escopo.
