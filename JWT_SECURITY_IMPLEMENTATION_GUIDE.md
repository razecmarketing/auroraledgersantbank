# Guia para corrigir `Unauthorized access – valid JWT token required`

Este documento contém **todo o conteúdo por extenso, sem comandos de terminal**, para você **copiar e colar**. Ele resolve o erro de autenticação ao fazer operações (ex.: depósito) integrando **Angular** (frontend) e **Spring Boot** (backend) com **JWT**.

> Como usar: crie/edite os arquivos do seu projeto conforme os trechos a seguir. Onde houver duas opções, **escolha uma** e remova a outra.



## 1) Frontend Angular

### 1.1 Proxy do Angular para o Spring (evita CORS e padroniza a URL `/api`)

**Arquivo**: `proxy.conf.json` (na raiz do projeto Angular)

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug"
  }
}
```

**Arquivo**: `package.json` (apenas os scripts relevantes)

```json
{
  "scripts": {
    "start": "ng serve proxyconfig proxy.conf.json"
  }
}
```

> A partir de agora, no seu código Angular, **sempre chame** o backend com **URL relativa** começando por `/api` (ex.: `/api/auth/login`, `/api/transactions/deposit`).



### 1.2 Serviço de autenticação (salva o token do login)

> Escolha **UMA** das opções abaixo, de acordo com como o backend devolve o token.

**Opção A – o token vem no corpo da resposta JSON**

**Arquivo**: `src/app/services/auth.service.ts`

```ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';

export interface LoginRequest { email: string; senha: string; }
export interface LoginResponse { token: string; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  constructor(private http: HttpClient) {}

  login(creds: LoginRequest) {
    return this.http.post<LoginResponse>('/api/auth/login', creds).pipe(
      tap(res => { if (res?.token) localStorage.setItem('access_token', res.token); })
    );
  }

  logout() { localStorage.removeItem('access_token'); }
  get token(): string | null { return localStorage.getItem('access_token'); }
  isAuthenticated(): boolean { return !!this.token; }
}
```

**Opção B – o token vem no cabeçalho `Authorization`**

**Arquivo**: `src/app/services/auth.service.ts`

```ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { tap } from 'rxjs/operators';

export interface LoginRequest { email: string; senha: string; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  constructor(private http: HttpClient) {}

  login(creds: LoginRequest) {
    return this.http.post('/api/auth/login', creds, { observe: 'response' }).pipe(
      tap((res: HttpResponse<any>) => {
        const raw = res.headers.get('Authorization'); // esperado: "Bearer <TOKEN>"
        const token = raw?.startsWith('Bearer ') ? raw.substring(7) : raw ?? null;
        if (token) localStorage.setItem('access_token', token);
      })
    );
  }

  logout() { localStorage.removeItem('access_token'); }
  get token(): string | null { return localStorage.getItem('access_token'); }
  isAuthenticated(): boolean { return !!this.token; }
}
```



### 1.3 Interceptor que adiciona `Authorization: Bearer <token>` em todas as requisições protegidas

> Versão **funcional** (Angular 16+). Se seu projeto usa `AppModule`, veja a forma de registro adiante.

**Arquivo**: `src/app/core/auth.interceptor.ts`

```ts
import { HttpInterceptorFn } from '@angular/common/http';

/** Endpoints públicos que NÃO exigem token */
const BYPASS = ['/api/auth/login', '/api/auth/register', '/actuator'];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (BYPASS.some(u => req.url.includes(u))) return next(req);

  const token = localStorage.getItem('access_token');
  const authReq = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;
  return next(authReq);
};
```

**Arquivo**: `src/app/core/logging.interceptor.ts` (opcional, ajuda a depurar)

```ts
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const loggingInterceptor: HttpInterceptorFn = (req, next) =>
  next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      console.error('[HTTP ERROR]', req.method, req.url, '→', err.status, err.statusText, 'body:', err.error);
      return throwError(() => err);
    })
  );
```

**Registro dos interceptors (standalone, recomendado em Angular 16+)**

**Arquivo**: `src/app/app.config.ts`

```ts
import { ApplicationConfig } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './core/auth.interceptor';
import { loggingInterceptor } from './core/logging.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(withInterceptors([authInterceptor, loggingInterceptor]))
  ]
};
```

**Registro alternativo (projetos com `AppModule`)**

**Arquivo**: `src/app/app.module.ts` (apenas o bloco `providers`)

```ts
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './core/auth.interceptor';
import { LoggingInterceptor } from './core/logging.interceptor';

// Caso use classes em vez de interceptors funcionais:
// { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
// { provide: HTTP_INTERCEPTORS, useClass: LoggingInterceptor, multi: true }

// Se mantiver a versão funcional, use provideHttpClient no módulo raiz em vez deste formato.
```

> **Importante**: com o proxy ativo, use sempre endpoints relativos (`/api/...`).



### 1.4 Exemplo de uso em um serviço de negócio (depósito)

**Arquivo**: `src/app/services/transactions.service.ts`

```ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface DepositoRequest { valor: number; descricao: string; }

@Injectable({ providedIn: 'root' })
export class TransactionsService {
  constructor(private http: HttpClient) {}

  depositar(req: DepositoRequest) {
    return this.http.post('/api/transactions/deposit', req);
  }
}
```



## 2) Backend Spring Boot

### 2.1 Dependências para JWT (pom.xml)

**Arquivo**: `pom.xml` (adicione as dependências a seguir)

```xml
<dependencies>
  <! ...suas dependências existentes... >

  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwtapi</artifactId>
    <version>0.11.5</version>
  </dependency>
  <dependency>
```
