## GIT_HELP.md (Fluxo Seguro & Reescrita Opcional)

### Fluxo Seguro Padrão
```
git switch -c authenticity-artifacts
git add .
git commit -m "docs(authenticity): add authenticity artifacts and CI workflow"
git push -u origin authenticity-artifacts
```

### Atualizar Branch com master (sem reescrever histórico público)
```
git fetch origin
git switch authenticity-artifacts
git merge origin/master   # ou rebase se preferir linearidade
```

### Reescrita de Histórico (Opcional, Risco)
Uso: remover commits antigos com conteúdo inflado. Não usar após branch já público para vários consumidores.

Ferramenta recomendada: git filter-repo (instalar previamente).

Exemplo (remover diretório antigo de UI):
```
git filter-repo --path archive/aurora-ledger-ui-archived --invert-paths
```

Depois da reescrita:
```
git push --force-with-lease origin master
```

### Boas Práticas
- Sempre gerar backup (`git clone --mirror`) antes de operações destrutivas.
- Preferir commits pequenos e descritivos.
- Atualizar RATIONALE_LOG.md junto de alterações arquiteturais.
