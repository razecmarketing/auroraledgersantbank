<!-- Backend-only scope: UI/client instructions were removed. -->
# JWT Security Notes (Backend Only)

Este repositório mantém apenas o backend Java/Spring Boot. Todas as instruções de UI foram removidas.

Pontos-chave para evitar `Unauthorized access – valid JWT token required`:
- Libere endpoints públicos: `/api/auth/**`, `/actuator/**`.
- Garanta que o filtro JWT seja aplicado após o `UsernamePasswordAuthenticationFilter`.
- Configure CORS para o(s) origin(s) de desenvolvimento válidos (ex.: http://localhost:8080 quando testando via ferramentas REST).
- Tokens devem ser assinados com HS512 e chave de 64 bytes; defina expiração adequada.
- Em testes manuais, inclua o header: `Authorization: Bearer <TOKEN>`.

Consulte a classe `SecurityConfig` e os testes de integração para o fluxo completo de autenticação.
