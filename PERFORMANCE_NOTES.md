## PERFORMANCE_NOTES.md (Template de Evidência)

Objetivo: Registrar resultados de testes de carga de forma rastreável.

### Ferramenta Recomendada
k6 (CLI) – executar local ou em pipeline.

### Cenário Base (Exemplo)
```
vus: 50
duração: 5m
alvo: POST /api/v1/deposits, POST /api/v1/payments, GET /api/v1/balance
```

### Script k6 Exemplo (pseudo)
```javascript
import http from 'k6/http';
import { check } from 'k6';
export const options = { vus: 50, duration: '5m' };
export default function() {
  const res = http.get('http://localhost:8080/api/v1/health');
  check(res, { 'status 200': r => r.status === 200 });
}
```

### Registro de Execução
Armazenar cada execução em `docs/perf/YYYY-MM-DD.md` no formato:
```
Data: 2025-09-23
Commit: <git-sha>
Config: vus=50, dur=5m
p95 latência(ms): <valor>
Erro (%): <valor>
Throughput (req/s): <valor>
Notas: <observações curtas>
```

### Critério de Regressão
Abrir issue quando p95 aumentar > 20% em relação a mediana das últimas 3 execuções.
