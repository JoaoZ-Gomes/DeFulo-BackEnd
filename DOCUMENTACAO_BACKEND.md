# Documentação do Backend DeFulo

## 1. Visão Geral
Este documento descreve o estado atual do backend do projeto DeFulo e propõe as próximas implementações para evoluir a plataforma.

### 1.1 Tecnologias principais
- Java 17
- Spring Boot 4.0.5
- Spring Data JPA
- Flyway
- Spring Security + JWT
- MapStruct
- PostgreSQL / H2
- Springdoc OpenAPI (Swagger)

## 2. Estado atual
O backend está em um estágio inicial funcional, com os principais recursos CRUD implementados e segurança JWT básica ativada.

### 2.1 Modelo de domínio
- `Usuario` como entidade base com herança `SINGLE_TABLE` e discriminadores.
- Perfis disponíveis:
  - `ADM`
  - `GESTOR`
  - `PREFEITURA`
  - `ENGENHEIRO`
  - `RTV`
  - `PRODUTOR`
- Subclasses especializadas:
  - `Produtor`
  - `Rtv`
  - `Engenheiro`
- Recursos agrícolas:
  - `Fazenda`
  - `Talhao`
  - `EventoManejo`

### 2.2 Endpoints existentes
#### Autenticação
- `POST /api/auth/login`

#### Cadastro aberto (onboarding)
- `POST /api/produtores`
- `POST /api/rtvs`
- `POST /api/engenheiros`

#### CRUD gerais
- `POST /api/usuarios`
- `GET /api/usuarios`
- `GET /api/usuarios/{id}`
- `PUT /api/usuarios/{id}`
- `DELETE /api/usuarios/{id}`

- `POST /api/produtores`
- `GET /api/produtores`
- `GET /api/produtores/{id}`
- `PUT /api/produtores/{id}`
- `DELETE /api/produtores/{id}`

- `POST /api/rtvs`
- `GET /api/rtvs`
- `GET /api/rtvs/{id}`
- `PUT /api/rtvs/{id}`
- `DELETE /api/rtvs/{id}`

- `POST /api/engenheiros`
- `GET /api/engenheiros`
- `GET /api/engenheiros/{id}`
- `PUT /api/engenheiros/{id}`
- `DELETE /api/engenheiros/{id}`

- `POST /api/fazendas`
- `GET /api/fazendas`
- `GET /api/fazendas/{id}`
- `GET /api/fazendas/por-produtor/{produtorId}`
- `PUT /api/fazendas/{id}`
- `DELETE /api/fazendas/{id}`

- `POST /api/talhoes`
- `GET /api/talhoes/{id}`
- `GET /api/talhoes/por-fazenda/{fazendaId}`
- `GET /api/talhoes/paginado/{fazendaId}`
- `PUT /api/talhoes/{id}`
- `DELETE /api/talhoes/{id}`

- `POST /api/eventos`
- `GET /api/eventos/{id}`
- `GET /api/eventos/por-talhao/{talhaoId}`
- `PUT /api/eventos/{id}`
- `DELETE /api/eventos/{id}`

### 2.3 Segurança
- Segurança configurada com `SecurityConfig`.
- Endpoints de login e cadastro de perfis específicos são públicos.
- Todos os outros endpoints exigem autenticação JWT.
- `SecurityFilter` valida o token e popula o contexto de segurança.
- `PasswordEncoder` usa `BCryptPasswordEncoder`.
- CORS liberado com `AllowedOriginPatterns: ["*"]`.

### 2.4 Inicialização de dados
A classe `DataInitializer` cria:
- usuário administrador: `admin@defulo.com / admin123`
- usuário produtor de teste: `produtor@defulo.com / demo`
- fazenda e talhão de exemplo vinculados ao produtor

## 3. Lacunas e pontos de melhoria
### 3.1 Falta de autorização granular
- Ainda não há regras de permissão por perfil nos endpoints.
- Todos os usuários autenticados têm acesso aos endpoints protegidos sem distinção de ação.

### 3.2 Falta de fluxo offline/sincronização
- Não existem endpoints nem modelo para sincronização offline.
- O backend não possui suporte a upload em lote / download incremental de dados.

### 3.3 Modelagem de domínio ainda parcial
- Não há entidade `Condomínio` ou gestão macro de associações.
- Não há recursos explícitos de plano técnico, gabarito ou diagnóstico.
- O evento de manejo existe, mas ainda não há histórico de auditoria avançado ou validação de unidade/quantidade.

### 3.4 Qualidade de API e resiliência
- Falta tratamento de exceção mais completo e respostas padronizadas.
- Falta testes unitários e de integração documentados.
- O tratamento de token expirado/inválido lança exceção genérica.
- CORS está muito aberto para produção.

## 4. Roadmap de evolução
### 4.1 Curto prazo
- Implementar autorização por perfil nos serviços e controllers.
- Adicionar filtros para produtor, engenheiro e RTV boqueados conforme dono/recurso.
- Criar serviço de validação de token com resposta 401/403 adequada.
- Adicionar testes unitários para controllers e serviços principais.

### 4.2 Médio prazo
- Implementar fluxo de sincronização offline:
  - endpoints de `sync/upload`
  - endpoints de `sync/download`
  - registro de versão/última atualização por recurso
- Adicionar entidade `Condomínio` e hierarquia macro.
- Expandir `EventoManejo` com dados técnicos e relacionamento com plano.
- Criar relatório de histórico por talhão e por produtor.

### 4.3 Longo prazo
- Adicionar refresh token e controle de sessão JWT.
- Reforçar CORS e políticas de segurança para produção.
- Documentar OpenAPI com exemplos para Flutter e web.
- Evoluir para dados offline robustos com controle de conflitos e reconciliação.

## 5. Observações importantes
- O backend atual já permite testes básicos de cadastro, autenticação e gestão de fazendas, talhões e eventos.
- A prioridade de evolução deve ser a segurança de acesso e a sincronização offline-first.
- O backend serve como base para conectar o app Flutter a um fluxo operacional de campo e escritório.

---

Documento gerado para a base `Backend/DeFulo-BackEnd` em 21 de maio de 2026.
