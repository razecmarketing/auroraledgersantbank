## CHECKLIST_CREDIBILITY.md (Pré-Push)

Marcar cada item antes de abrir PR / push principal.

### Identidade & Clareza
- [ ] README sem afirmações absolutas sem evidência.
- [ ] Sem listas de múltiplos mentores / personas.
- [ ] Apenas "CEZI COLA Senior Software Engineer" como mantenedor.

### Artefatos Presentes
- [ ] DECISIONS.md atualizado
- [ ] SCOPE_LIMITATIONS.md atualizado
- [ ] RATIONALE_LOG.md última entrada reflete mudança atual
- [ ] THREAT_MODEL.md revisado se novo ativo afetado
- [ ] PERFORMANCE_NOTES.md (se mudança impacta desempenho)
- [ ] CHECKLIST_CREDIBILITY.md (este arquivo) revisto

### Segurança & Observabilidade
- [ ] Segredo JWT não hardcoded em git (usa env var)
- [ ] Logs não imprimem PII / segredos
- [ ] Métricas básicas expostas (latência, erro) ou ticket aberto

### Testes
- [ ] Invariáveis críticas cobertas (saldo ≥ 0, idempotência planejada)
- [ ] Nenhum teste flakey conhecido sem anotação de TODO

### CI
- [ ] Pipeline executa build + testes sem falha
- [ ] jacoco.xml gerado (mesmo sem badge ainda)

### Pós-Verificação
- [ ] Commit message factual (sem adjetivos)
- [ ] RATIONALE_LOG.md atualizado

Falha em algum item? Adiar push ou criar issue explicando.
