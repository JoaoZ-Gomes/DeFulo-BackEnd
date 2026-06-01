#!/bin/bash
# API Sync Endpoints - DeFulo Backend
# 
# This file documents all the sync endpoints and includes curl examples
# for testing locally.

# ============================================================================
# 1. HEALTH CHECK
# ============================================================================
# Purpose: Verify server is reachable (V2 verification in ConnectivityService)
# Used by: Client every time before starting sync
# Timeout: 3 seconds

echo "1️⃣ Testing Health Check..."
curl -X GET http://localhost:8080/api/health \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n\n"

# Expected Response:
# {
#   "status": "ok",
#   "timestamp": "2026-05-29T10:00:00Z",
#   "version": "1.0"
# }

# ============================================================================
# 2. PUSH SYNC (Client → Server)
# ============================================================================
# Purpose: Send queued operations from client
# Used by: SyncEngine.startSync() after connectivity checks
# Timeout: Per item ~10 seconds

echo "2️⃣ Testing Push Sync..."
curl -X POST http://localhost:8080/api/sync/push \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "deviceId": "device-uuid-12345",
    "items": [
      {
        "localId": "ae3f4e2c-91d2-4c7a-9e5b-1d3c2f4e5a6b",
        "entityType": "evento",
        "operation": "CREATE",
        "payload": {
          "talhaoId": 42,
          "tipoEvento": "PULVERIZAÇÃO",
          "dataEvento": "2026-05-29",
          "responsavel": "João Silva",
          "notas": "Aplicado defensivo XYZ"
        },
        "localVersion": 1,
        "checksum": "sha256_hash_of_payload_here",
        "createdAt": "2026-05-29T10:00:00Z"
      },
      {
        "localId": "42",
        "entityType": "talhao",
        "operation": "UPDATE",
        "payload": {
          "id": 42,
          "nome": "Talhão A - Atualizado",
          "area": "15.5",
          "cultura": "Milho"
        },
        "localVersion": 2,
        "checksum": "sha256_hash_of_payload_here",
        "createdAt": "2026-05-29T10:05:00Z"
      }
    ]
  }' \
  -w "\nStatus: %{http_code}\n\n"

# Expected Response:
# {
#   "syncId": "sync-uuid-789-abc",
#   "items": [
#     {
#       "localId": "ae3f4e2c-91d2-4c7a-9e5b-1d3c2f4e5a6b",
#       "status": "SUCCESS",
#       "remoteId": 1042
#     },
#     {
#       "localId": "42",
#       "status": "SUCCESS",
#       "remoteId": 42
#     }
#   ],
#   "totalProcessed": 2,
#   "successCount": 2,
#   "conflictCount": 0,
#   "errorCount": 0
# }

# ============================================================================
# 3. PULL SYNC (Server → Client)
# ============================================================================
# Purpose: Fetch entities modified on server since last sync
# Used by: SyncEngine._performPullSync() after successful push
# Timeout: 15 seconds
# Parameter: since = timestamp in ISO 8601 format

echo "3️⃣ Testing Pull Sync..."
SINCE="2026-05-29T09:00:00Z"
curl -X GET "http://localhost:8080/api/sync/pull?since=${SINCE}" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

# Expected Response:
# {
#   "items": [
#     {
#       "remoteId": 42,
#       "entityType": "talhao",
#       "operation": "UPDATE",
#       "data": {
#         "id": 42,
#         "nome": "Talhão A - Versão Servidor",
#         "area": "16.0",
#         "cultura": "Soja",
#         "status": "ATIVA"
#       },
#       "updatedAt": "2026-05-29T12:30:00Z",
#       "version": 3
#     }
#   ],
#   "serverTimestamp": "2026-05-29T12:35:00Z"
# }

# ============================================================================
# 4. SYNC STATUS (Device → Server)
# ============================================================================
# Purpose: Check last successful sync for a device
# Used by: UI to display last sync info
# Used by: Idempotency check (V6)
# Timeout: 3 seconds

echo "4️⃣ Testing Sync Status..."
DEVICE_ID="device-uuid-12345"
curl -X GET "http://localhost:8080/api/sync/status/${DEVICE_ID}" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

# Expected Response:
# {
#   "deviceId": "device-uuid-12345",
#   "lastSyncId": "sync-uuid-789-abc",
#   "lastSyncStatus": "DONE",
#   "lastSyncTimestamp": "2026-05-29T12:35:00Z",
#   "pendingItemsCount": 0
# }

# ============================================================================
# 5. TOKEN REFRESH
# ============================================================================
# Purpose: Refresh expired JWT token
# Used by: ConnectivityService._verifyAuthentication (V3)
# Used by: SyncEngine if token expires during sync
# Timeout: 5 seconds

echo "5️⃣ Testing Token Refresh..."
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_JWT_TOKEN_HERE"
  }' \
  -w "\nStatus: %{http_code}\n\n"

# Expected Response:
# {
#   "token": "new_jwt_token_here"
# }

# ============================================================================
# ERROR SCENARIOS
# ============================================================================

echo "⚠️ Testing Error Scenarios..."

# 409 Conflict (Version conflict)
echo "→ 409 Conflict (OCC violation)..."
curl -X POST http://localhost:8080/api/sync/push \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "deviceId": "device-uuid-12345",
    "items": [
      {
        "localId": "42",
        "entityType": "talhao",
        "operation": "UPDATE",
        "payload": {"id": 42, "area": "15.0"},
        "localVersion": 1,
        "checksum": "abc123",
        "createdAt": "2026-05-29T10:00:00Z"
      }
    ]
  }' \
  -w "\nStatus: %{http_code}\n\n"

# Expected: 409 with conflicting field details

# 401 Unauthorized (Token expired)
echo "→ 401 Unauthorized (Expired token)..."
curl -X GET http://localhost:8080/api/sync/pull?since=2026-05-29T09:00:00Z \
  -H "Authorization: Bearer INVALID_TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

# Expected: 401 with message "Token inválido ou expirado"

# 422 Unprocessable Entity (Invalid data)
echo "→ 422 Unprocessable (Invalid payload)..."
curl -X POST http://localhost:8080/api/sync/push \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "deviceId": "device-uuid-12345",
    "items": [
      {
        "localId": "invalid",
        "entityType": "",
        "operation": "INVALID_OP",
        "payload": {}
      }
    ]
  }' \
  -w "\nStatus: %{http_code}\n\n"

# Expected: 422 with validation errors

# ============================================================================
# TESTING WITH INSOMNIA/POSTMAN
# ============================================================================
# 
# 1. Create collection named "DeFulo Sync"
# 2. Add requests:
#    - GET  /api/health
#    - POST /api/sync/push
#    - GET  /api/sync/pull
#    - GET  /api/sync/status/:deviceId
#    - POST /api/auth/refresh
# 
# 3. Set environment variables:
#    - base_url: http://localhost:8080
#    - jwt_token: <your-token>
#    - device_id: <device-uuid>
# 
# 4. Use pre-request scripts to:
#    - Generate deviceId if missing
#    - Calculate SHA-256 checksums
#    - Format timestamps correctly

# ============================================================================
# LOAD TESTING
# ============================================================================
# 
# For testing performance with 1000 items:
# 
# wrk -t12 -c400 -d30s -s push_load_test.lua http://localhost:8080/api/sync/push
# 
# wrk -t12 -c400 -d30s http://localhost:8080/api/health

# ============================================================================
# NOTES
# ============================================================================
# 
# 1. All timestamps must be in ISO 8601 UTC format
# 2. Authorization header required for sync endpoints (except /api/health)
# 3. Checksum must be lowercase hex string (SHA-256)
# 4. Device ID should persist across app restarts
# 5. Retry logic: Server doesn't retry, client handles retries
# 6. Batch size: Recommended max 100 items per push
# 7. Rate limiting: Not implemented yet, but plan for 1000 req/min per device
