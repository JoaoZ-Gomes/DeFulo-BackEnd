# DeFulo — Backend

API Spring Boot do sistema DeFulo. Para o contexto de negócio, veja `context.md`; para detalhes de arquitetura, `DOCUMENTACAO_BACKEND.md`.

## Configuração local

O banco de dados (Postgres/Neon) é configurado via variáveis de ambiente — não há credenciais no repositório.

Defina antes de rodar a aplicação:

```
DB_URL=jdbc:postgresql://<host>/<database>?sslmode=require
DB_USERNAME=<usuario>
DB_PASSWORD=<senha>
```

Exemplo (PowerShell):

```powershell
$env:DB_URL = "jdbc:postgresql://<host>/<database>?sslmode=require"
$env:DB_USERNAME = "<usuario>"
$env:DB_PASSWORD = "<senha>"
./mvnw.cmd spring-boot:run
```

Exemplo (bash):

```bash
export DB_URL="jdbc:postgresql://<host>/<database>?sslmode=require"
export DB_USERNAME="<usuario>"
export DB_PASSWORD="<senha>"
./mvnw spring-boot:run
```

Ao subir, o Flyway aplica as migrations em `src/main/resources/db/migration` automaticamente (`spring.flyway.enabled=true`).

## Armazenamento de arquivos

Fotos de evidência de inspeções são salvas em disco local, no caminho definido por `app.storage.base-path`
(padrão: `./storage`, ignorado pelo git). Para múltiplas instâncias atrás de um load balancer, trocar
`FileStorageService` por um backend de objeto (S3/GCS/MinIO) mantendo a mesma assinatura.
