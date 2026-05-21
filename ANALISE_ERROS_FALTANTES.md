# Análise de Erros e Funcionalidades Faltantes - Backend DeFulo

## 1. ERROS CRÍTICOS E VULNERABILIDADES

### 1.1 ⚠️ CRÍTICO: Credenciais hardcoded em `application.properties`
**Localização:** `src/main/resources/application.properties`

**Problema:**
```properties
spring.datasource.username=neondb_owner
spring.datasource.password=npg_1fDmc2kUMOJZ
```
As credenciais do banco de dados estão em texto plano no repositório.

**Impacto:** 🔴 CRÍTICO
- Vulnerabilidade de segurança grave
- Qualquer pessoa com acesso ao repositório tem credenciais do banco

**Solução recomendada:**
```properties
# Usar variáveis de ambiente
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# OU usar Spring Cloud Config
# OU usar AWS Secrets Manager / Azure Key Vault
```

---

### 1.2 ⚠️ CRÍTICO: Sem tratamento de token inválido/expirado
**Localização:** `infrastructure/security/TokenService.java`

**Problema:**
```java
public String getSubject(String tokenJWT) {
    try {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(tokenJWT)
                .getBody();
        return claims.getSubject();
    } catch (Exception e) {
        throw new RuntimeException("Token JWT inválido ou expirado!");
    }
}
```
Lança `RuntimeException` genérica, não permitindo tratamento apropriado no `ErrorHandler`.

**Impacto:** 🔴 CRÍTICO
- Respostas inconsistentes para token inválido
- Cliente não consegue distinguir entre 401 e erro de servidor

**Solução recomendada:**
```java
throw new InvalidTokenException("Token JWT inválido ou expirado!");
// Criar novo tipo de exceção
// Adicionar handler em ErrorHandler retornando 401
```

---

### 1.3 ⚠️ CRÍTICO: Falta de autorização por perfil
**Localização:** Todos os controllers e serviços

**Problema:**
- Produtor consegue listar/deletar fazendas de outros produtores
- RTV consegue criar eventos para talhões de qualquer um
- Não há validação de propriedade de recurso

**Exemplos:**
```java
// FazendaService.java - QUALQUER usuário autenticado consegue ver TODAS as fazendas
@Transactional(readOnly = true)
public Page<FazendaResponseDTO> listar(Pageable pageable) {
    return repository.findAll(pageable).map(mapper::toResponseDTO);
}

// Deveria ser:
@Transactional(readOnly = true)
public Page<FazendaResponseDTO> listar(Pageable pageable) {
    Long produtorId = getUsuarioAutenticado().getId();
    return repository.findByProdutorId(produtorId, pageable).map(mapper::toResponseDTO);
}
```

**Impacto:** 🔴 CRÍTICO
- Violação total de isolamento de dados
- Produtores conseguem acessar dados uns dos outros

**Solução recomendada:**
Implementar validação em serviço ou usar `@PreAuthorize` com SpEL:
```java
@GetMapping
@PreAuthorize("@authorizationService.canViewFazenda(#id)")
public ResponseEntity<Page<FazendaResponseDTO>> listar(Pageable pageable) {
    // ...
}
```

---

## 2. ERROS DE IMPLEMENTAÇÃO

### 2.1 ❌ Sem método para obter usuário autenticado nos serviços
**Localização:** Todos os serviços

**Problema:**
Não há forma de saber qual usuário está autenticado dentro dos serviços:
```java
// Como saber qual produtor está fazendo a requisição?
// Os serviços não têm acesso ao usuário autenticado
public ProdutorResponseDTO atualizar(Long id, ProdutorUpdateRequestDTO dto) {
    // Precisa do usuário autenticado para validar propriedade
    // MAS NÃO TEM FORMA DE PEGAR
}
```

**Solução recomendada:**
Criar util para pegar usuário autenticado:
```java
@Component
public class SecurityUtils {
    public static Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Usuario) {
            return (Usuario) auth.getPrincipal();
        }
        throw new RuntimeException("Usuário não autenticado");
    }
}
```

---

### 2.2 ❌ Validação de CPF não implementada
**Localização:** `features/usuario/service/UsuarioService.java` e serviços correlatos

**Problema:**
```java
// DTOs aceitam CPF, mas não validam formato
@Size(min = 11, max = 14, message = "O CPF deve ter entre 11 e 14 caracteres.")
String cpf,
```
Valida apenas tamanho, não valida dígitos verificadores.

**Impacto:** 🟠 MÉDIO
- CPF inválido pode ser armazenado
- Dados incorretos no banco

**Solução recomendada:**
```java
@ValidCPF  // Criar custom validator
String cpf,

// Implementar validator customizado
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CPFValidator.class)
public @interface ValidCPF {
    String message() default "CPF inválido";
    // ...
}
```

---

### 2.3 ❌ Sem refresh token
**Localização:** `infrastructure/security/TokenService.java`

**Problema:**
Token JWT expira em 2 horas e usuário é deslogado automaticamente. Sem refresh token, não há forma de renovar sessão sem fazer login novamente.

**Token duration:**
```java
return java.util.Date.from(
    LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"))
);
```

**Impacto:** 🟠 MÉDIO
- UX ruim para app mobile
- Usuário é forçado a fazer login a cada 2 horas

**Solução recomendada:**
Implementar refresh token:
```java
// Guardar refresh token em db_refresh_tokens
// Endpoint: POST /api/auth/refresh
// Aceitar refresh token e retornar novo access token
```

---

### 2.4 ❌ Discriminador não definido corretamente nas subclasses
**Localização:** `features/usuario/model/Usuario.java`

**Problema:**
```java
@DiscriminatorValue("PRODUTOR")  // ✓ OK
@DiscriminatorValue("RTV")       // ✓ OK
@DiscriminatorValue("ENGENHEIRO")  // ✓ OK

// MAS: classe Usuario base não tem @DiscriminatorValue
// O que acontece quando cria um Usuario base?
```

**Impacto:** 🟡 BAIXO
- Usuários base podem ter valor NULL ou undefined no discriminador

**Solução recomendada:**
Adicionar `@DiscriminatorValue("USUARIO")` ou proibir criação de Usuario base.

---

### 2.5 ❌ Email como chave de busca, mas Perfil em endpoint distinto
**Localização:** Controllers de Produtor, RTV, Engenheiro

**Problema:**
```java
// Todos usam email como identificador único
usuarioRepository.existsByEmail(dto.email())

// MAS: endpoints são separados
POST /api/produtores
POST /api/rtvs
POST /api/engenheiros

// Usuário não sabe qual endpoint usar se souber apenas o email
// Conflito potencial de duplo cadastro
```

**Impacto:** 🟠 MÉDIO
- Confusão na API
- Usuário pode tentar criar conta em vários endpoints com mesmo email

---

### 2.6 ❌ Eventos sem referência a usuário criador
**Localização:** `features/evento/model/EventoManejo.java`

**Problema:**
```java
@ManyToOne
@JoinColumn(name = "rtv_id")
private Rtv rtv;  // Referência manual ao RTV, não automática
```
Comentário no `EventoMapper`:
```java
@Mapping(target = "rtv", ignore = true)    // RTV vem do usuário autenticado (futuro)
```
Isso significa que RTV **não está sendo setado automaticamente**.

**Impacto:** 🟠 MÉDIO
- Não há rastreamento de quem criou o evento
- Auditoria impossível
- Dados de responsabilidade não preenchidos

**Solução recomendada:**
```java
@PrePersist
void setRtvAutomaticamente() {
    if (this.rtv == null) {
        Usuario autenticado = SecurityUtils.getUsuarioAutenticado();
        if (autenticado instanceof Rtv) {
            this.rtv = (Rtv) autenticado;
        }
    }
}
```

---

## 3. FUNCIONALIDADES FALTANDO

### 3.1 ❌ Sem sincronização offline-first
**Problema:**
Nenhum endpoint para sincronização de dados. Segundo o `context.md`, o sistema deve ter offline-first.

**Faltam endpoints:**
```
POST /api/sync/upload      - Cliente envia eventos offline acumulados
GET  /api/sync/download    - Cliente baixa dados atualizados
POST /api/sync/pull        - Sincronização bidirecional incremental
GET  /api/sync/status      - Status da última sincronização
```

**Impacto:** 🔴 CRÍTICO (por design)
- Sem sincronização offline, o app não funciona no campo sem internet

**O que precisa:**
- Tabela de `sync_metadata` para rastrear versões
- Endpoint de conflito/reconciliação
- Controle de timestamp de atualização

---

### 3.2 ❌ Sem entidade Condomínio
**Problema:**
Hierarquia incompleta. Segundo `context.md`:
```
1. Condomínio (gestão macro)
2. Fazenda (unidade do produtor)
3. Talhão (unidade técnica)
```
Mas `Condomínio` não existe.

**Faltam:**
```
Model: Condomínio
  - id
  - nome
  - descricao
  - endereco
  - associacao_id (referência a órgão gestor)
  - data_criacao

Relacionamento:
  Fazenda.condominio_id → Condomínio.id
```

**Impacto:** 🔴 CRÍTICO (por design)
- Gestão macro impossível
- Relatórios por condomínio não funcionam

---

### 3.3 ❌ Sem plano técnico / gabarito
**Problema:**
Engenheiro deve definir "gabarito" (plano técnico) para cada cultura/talhão, mas não há entidade para isso.

**Faltam:**
```
Model: PlanoTecnico
  - id
  - talhao_id
  - engenheiro_id
  - cultura
  - param_umidade_critica
  - param_ph_solo
  - manejo_esperado (lista de eventos esperados)
  - data_criacao

Model: GabaritoCultura
  - id
  - cultura (ex: "Soja")
  - parametros_tecnicos (JSON)
  - manejos_esperados (lista)
```

**Impacto:** 🔴 CRÍTICO (por design)
- Diagnóstico não validável
- Sem comparação entre plano e realidade

---

### 3.4 ❌ Sem relatórios
**Problema:**
Faltam endpoints de relatório para produtor, engenheiro e administrativos.

**Faltam endpoints:**
```
GET /api/relatorios/produtividade/{talhaoId}  - Histórico de produção
GET /api/relatorios/manejo/{produtorId}        - Histórico de manejos
GET /api/relatorios/conformidade/{talhaoId}    - Conformidade com plano técnico
GET /api/relatorios/condominio/{condominioId}  - Agregado por condomínio
```

**Impacto:** 🔴 CRÍTICO (por design)
- Sem visualização de dados
- Sem inteligência de negócio

---

### 3.5 ❌ Sem testes automatizados
**Problema:**
Único teste é `contextLoads()` vazio.

```java
@SpringBootTest
class ApiApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

**Faltam:**
- Testes unitários de serviços (CRUD, validações)
- Testes de integração (controllers com MockMvc)
- Testes de autenticação
- Testes de autorização
- Testes de regra de negócio

**Impacto:** 🔴 CRÍTICO
- Sem confiança em mudanças
- Regressões não detectadas

**Exemplo do que deveria existir:**
```java
@SpringBootTest
class ProdutorServiceTest {
    @Test
    void criarComEmailDuplicado_DeveLancarExcecao() { }
    
    @Test
    void atualizarValidaPropriedade() { }
    
    @Test
    void buscarApenasDoProdutor() { }
}

@WebMvcTest(ProdutorController.class)
class ProdutorControllerTest {
    @Test
    void criar_ComDadosValidos_Retorna201() { }
    
    @Test
    void criar_SemAutenticacao_Retorna201() { }  // Endpoint aberto
}
```

**Impacto:** 🔴 CRÍTICO
- Confiabilidade comprometida

---

### 3.6 ❌ Sem logs estruturados
**Problema:**
Não há logging configurado. Debugging em produção é impossível.

**Faltam:**
```properties
# logback-spring.xml não existe
# Sem logger.info / logger.error / logger.debug

# Sem tracking de requisição (X-Request-ID)
# Sem logging de autenticação
# Sem logging de operações críticas
```

**Impacto:** 🟠 MÉDIO
- Debugging impossível em produção
- Auditoria não rastreável

---

### 3.7 ❌ Sem validação de integridade referencial
**Problema:**
Alguns relacionamentos faltam `CONSTRAINT CHECK` ou validação em serviço.

**Exemplos:**
```sql
-- V1: talhao_id em usuarios não tem FK
talhao_id BIGINT,  -- Sem CONSTRAINT

-- V3: rtv_id permite NULL
rtv_id BIGINT,  -- ON DELETE SET NULL

-- Deveria validar que RTV está ativo/válido
```

**Impacto:** 🟠 MÉDIO
- Dados órfãos no banco
- Referências inválidas

---

### 3.8 ❌ Sem versionamento de API
**Problema:**
Todos endpoints em `/api/`, sem versionamento.

```
/api/usuarios      # Versão ???
/api/fazendas      # Versão ???
```

**Solução recomendada:**
```
/api/v1/usuarios
/api/v2/usuarios   # Com mudanças breaking
```

**Impacto:** 🟡 BAIXO
- Problema futuro quando precisar evoluir API

---

## 4. PROBLEMAS DE QUALIDADE

### 4.1 ❌ CORS muito aberto
**Localização:** `infrastructure/security/SecurityConfig.java`

```java
configuration.setAllowedOriginPatterns(List.of("*"));
```

**Problema:**
Em produção, isso permite CSRF de qualquer origem.

**Solução recomendada:**
```properties
app.cors.allowed-origins=https://app.defulo.com,https://web.defulo.com
```

---

### 4.2 ❌ Sem validação de quantidade de eventos por talhão
**Problema:**
Campo `quantidade` em `EventoManejo` é string genérica.

```java
private String quantidade;  // 🔴 Qualquer coisa entra
```

Deveria ter:
- Unidade (kg, L, m³)
- Valor numérico validado
- Validação mínimo/máximo

---

### 4.3 ❌ Sem tratamento de exceção para SQL/Database
**Localização:** `infrastructure/exception/ErrorHandler.java`

**Problema:**
```java
@RestControllerAdvice
public class ErrorHandler {
    // Sem handler para DataIntegrityViolationException
    // Sem handler para PersistenceException
    // Sem handler para SQLException
}
```

Se houver erro de integridade, cliente recebe erro genérico de servidor.

**Solução recomendada:**
```java
@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(...) {
    // Tratar violação de constraint com mensagem amigável
}
```

---

## 5. PROBLEMAS DE DESIGN

### 5.1 ❌ Falta de soft delete
**Problema:**
Todos os deletes são físicos. Auditoria histórica perdida.

```java
public void excluir(Long id) {
    repository.deleteById(id);  // 🔴 Perdido para sempre
}
```

**Solução recomendada:**
Adicionar `deletado: boolean` e usar `WHERE deletado = false` em queries.

---

### 5.2 ❌ Sem audit trail
**Problema:**
Mudanças em recursos não são registradas. Quem fez o quê e quando é desconhecido.

**Deveria ter:**
```
Model: AuditLog
  - id
  - usuario_id
  - recurso (ex: "TALHAO")
  - recurso_id
  - acao (CREATE, UPDATE, DELETE)
  - dados_antes
  - dados_depois
  - timestamp
```

**Impacto:** 🔴 CRÍTICO (por compliance)
- Sem rastreabilidade

---

### 5.3 ❌ Sem transações distribuídas
**Problema:**
Se sincronização offline incluir múltiplos recursos, não há atomicidade.

**Exemplo:**
```
Upload contém:
  - 5 eventos
  - 2 talhões atualizados
  
Se falhar no 3º evento, qual é o estado do banco?
Faltam transações distribuídas / saga pattern.
```

---

## 6. CHECKLIST DE CORREÇÕES PRIORITÁRIAS

### CRÍTICAS (Fazer agora)
- [ ] 🔴 Mover credenciais para variáveis de ambiente
- [ ] 🔴 Implementar autorização por perfil em todos serviços
- [ ] 🔴 Criar tratamento de token inválido (InvalidTokenException)
- [ ] 🔴 Criar método para obter usuário autenticado
- [ ] 🔴 Adicionar RTV automático em EventoManejo
- [ ] 🔴 Implementar sincronização offline básica

### ALTAS (Semana 1-2)
- [ ] 🟠 Implementar validação de CPF
- [ ] 🟠 Criar testes unitários e integração
- [ ] 🟠 Adicionar entidade Condomínio
- [ ] 🟠 Reforçar CORS para produção
- [ ] 🟠 Adicionar handlers de exceção para DB

### MÉDIAS (Semana 3-4)
- [ ] 🟡 Implementar refresh token
- [ ] 🟡 Criar plano técnico / gabarito
- [ ] 🟡 Adicionar endpoints de relatório
- [ ] 🟡 Configurar logs estruturados
- [ ] 🟡 Adicionar soft delete e audit trail

---

## 7. DOCUMENTO DE RISCOS

| Risco | Severidade | Probabilidade | Impacto | Mitigation |
|-------|-----------|----------------|---------|-----------|
| Dados expostos (credenciais) | 🔴 CRÍTICO | Alta | Completo comprometimento | Usar env vars |
| Isolamento de dados quebrado | 🔴 CRÍTICO | Alta | Produtores veem uns dos outros | Implementar autorização |
| Token inválido não tratado | 🔴 CRÍTICO | Alta | Respostas inconsistentes | Criar InvalidTokenException |
| Sem auditoria | 🔴 CRÍTICO | Média | Impossível rastrear mudanças | Implementar AuditLog |
| Sem sincronização offline | 🔴 CRÍTICO | Alta | App não funciona em campo | Implementar endpoints sync |
| Sem testes | 🔴 CRÍTICO | Alta | Regressões não detectadas | Adicionar testes |

---

Documento gerado em 21 de maio de 2026.
