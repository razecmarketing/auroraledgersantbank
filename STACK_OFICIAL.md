# Aurora Ledger Santander  Stack Oficial

**Sistema Bancário Multinacional | Versão Definitiva | Setembro 2025**



## Resumo Executivo

Sistema bancário moderno com arquitetura CQRS, compliance PCI DSS/LGPD, e infraestrutura containerizada para alta disponibilidade e escalabilidade.



## 1. Backend (Java / Spring Boot)

### Stack Core
 **Java 17** (OpenJDK LTS)
 **Spring Boot 3.5.6**
 **Maven 3.9.6**

### Responsabilidades
 Core da aplicação bancária
 APIs REST (contas, transações, usuários, segurança JWT)
 Arquitetura em camadas:
  `domain` -> Regras de negócio e entidades
  `application` -> Casos de uso e orquestração
  `infrastructure` -> Persistência e integrações
  `interfaces` -> Controllers e DTOs

### Principais Dependências
```xml
 Spring Boot Starter Web
 Spring Boot Starter Security  
 Spring Boot Starter Data MongoDB
 Spring Boot Starter Data Redis
 Spring Boot Starter Validation
 JWT (io.jsonwebtoken)
 BCrypt (Spring Security)
 JaCoCo (100% test coverage)
 JUnit 5 + Mockito + RestAssured
```

### Padrões Arquiteturais
 **CQRS** (Command Query Responsibility Segregation)
 **DDD** (Domain Driven Design)
 **Event Sourcing** (MongoDB como Event Store)
 **Clean Architecture**
 **Optimistic Locking** para concorrência



## 2. Banco de Dados

### MongoDB -> Event Store
 **Versão**: 7.x (Docker oficial)
 **Responsabilidade**: Persistência de eventos de transações
 **Configuração**: Replica Set para alta disponibilidade
 **Volumes**: Dados persistentes em `/data/db`

### Redis -> Query/Cache para CQRS
 **Versão**: 7.x (Docker oficial)  
 **Responsabilidade**: Cache de consultas e otimização CQRS
 **Configuração**: AOF + RDB persistence
 **Uso**: Sessions, consultas frequentes, dados temporários

### Estratégia de Dados
 **Escrita**: MongoDB (consistência forte, eventos imutáveis)
 **Leitura**: Redis (performance, cache inteligente)
 **Backup**: Snapshots automáticos + replicação



<!-- Section intentionally omitted to comply with backend-only scope -->



## 4. Infraestrutura (DevOps)

### Prérequisitos
 **Docker Desktop** ativo permanentemente
 **Docker Compose** para orquestração

### Containers Orquestrados
```yaml
services:
  appbackend:     # Spring Boot Application
  mongodb:         # Event Store principal
  redis:           # Cache CQRS
```

### Características Técnicas
 **Volumes persistentes**: Dados não se perdem entre restarts
 **Rede interna**: Comunicação segura entre containers
 **Health checks**: Monitoramento automático de saúde
 **Restart policies**: Autorecuperação em falhas

### Configuração de Ambiente
```bash
# Inicialização completa
dockercompose up d

# Logs em tempo real
dockercompose logs f

# Parada controlada
dockercompose down
```



## 5. Segurança & Compliance

### Padrões Implementados
 **PCI DSS**: Tokenização de dados sensíveis
 **LGPD/GDPR**: Anonimização e mascaramento
 **AML/KYC**: Prevenção de lavagem de dinheiro
 **PSD2**: Autenticação forte (MFA, OAuth2)

### Medidas Técnicas
 JWT com expiração automática
 BCrypt para hash de senhas
 Logs auditáveis sem dados sensíveis
 CPFs mascarados em todas as saídas
 Rate limiting e proteção DDoS



## 6. Qualidade & Testes

### Estratégia de Testes
 **Cobertura**: 100% obrigatória (JaCoCo enforcement)
 **TDD**: Testes primeiro, implementação depois
 **Tipos**: Unitários, Integração, E2E

### Suítes de Teste Implementadas
1. **CPF Validation**: 15 testes nomeados
2. **Banking Business Rules**: 15 testes nomeados  
3. **JWT Security**: 15 testes nomeados
4. **Concurrency/Optimistic Locking**: 10 testes nomeados
5. **API Integration**: 10 testes nomeados

**Total**: 65 testes nomeados cobrindo todos os cenários críticos



## 7. Fluxo de Desenvolvimento

### Comandos Essenciais
```bash
# Backend (Maven)
mvn clean compile          # Compilação
mvn clean test            # Execução de testes
mvn clean test jacoco:report  # Relatório de cobertura

# Infraestrutura (Docker)
dockercompose up d      # Subir ambiente
dockercompose logs f    # Monitorar logs
dockercompose down       # Parar ambiente
```

### CI/CD Ready
 Pipeline automatizado
 Quality gates com JaCoCo
 Deployment containerizado
 Rollback automático em falhas



## 8. Escalabilidade Futura

### Arquitetura Preparada Para
 **Microservices**: Monólito modular facilmente divisível
 **Event Streaming**: Kafka para highthroughput
 **Multiregion**: Database sharding e replicação
 **Enterprise Logging**: Logs estruturados para auditoria
 **API Gateway**: Kong ou Spring Cloud Gateway



## 9. Métricas de Sucesso

### Performance
 Latência p95 < 200ms
 Throughput > 1000 TPS
 Uptime > 99.9%

### Qualidade
 Cobertura de testes: 100%
 Bugs críticos: 0
 Vulnerabilidades: 0

### Compliance
 Auditoria automatizada
 Logs completos e seguros
 Conformidade regulatória contínua



## 10. Contatos e Suporte

### Arquitetura
 **Lead**: Maintainer
 **Compliance**: Maintainer
 **DevOps**: Maintainer

### Documentação Técnica
 API: `/swaggerui.html`
 Coverage: `/target/site/jacoco/index.html`
 Logs: `dockercompose logs`



**Stack validada em Setembro 2025 | Compliance bancário multinacional**
