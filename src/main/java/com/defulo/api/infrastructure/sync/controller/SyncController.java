package com.defulo.api.infrastructure.sync.controller;

import com.defulo.api.infrastructure.sync.dto.*;
import com.defulo.api.infrastructure.sync.service.SyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

/**
 * Controller para endpoints de sincronização offline-first
 * 
 * Responsável por:
 * - Receber operações em lote (push) do cliente
 * - Enviar dados atualizados (pull) para o cliente
 * - Gerenciar estado de sincronização por dispositivo
 * - Verificação de saúde do servidor (health check)
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Sincronização", description = "Endpoints para sincronização offline-first")
public class SyncController {

    private final SyncService syncService;

    /**
     * Health check para verificação de conectividade
     * 
     * Utilizado pelo cliente como V2 (verificação de alcançabilidade)
     * Responde rapidamente para confirmar que o servidor está acessível
     */
    @GetMapping("/health")
    @Operation(summary = "Health check do servidor", description = "Verifica se o servidor está disponível")
    public ResponseEntity<HealthCheckResponse> health() {
        log.debug("Health check solicitado");
        return ResponseEntity.ok(new HealthCheckResponse(
            "ok",
            OffsetDateTime.now().toString(),
            "DeFulo Backend v1.0"
        ));
    }

    /**
     * Push: Receber operações em lote do cliente
     * 
     * Processa criações, atualizações e exclusões de forma idempotente.
     * Detecta e resolve conflitos usando estratégias configuradas.
     * 
     * POST /api/sync/push
     * {
     *   "deviceId": "uuid-do-dispositivo",
     *   "items": [
     *     {
     *       "localId": "uuid-local",
     *       "entityType": "evento",
     *       "operation": "CREATE",
     *       "payload": { ... },
     *       "localVersion": 1,
     *       "checksum": "sha256...",
     *       "createdAt": "2026-05-29T10:00:00Z"
     *     }
     *   ]
     * }
     */
    @PostMapping("/sync/push")
    @Operation(
        summary = "Sincronizar operações do cliente",
        description = "Recebe lote de operações (CREATE/UPDATE/DELETE) para processar no servidor"
    )
    public ResponseEntity<SyncPushResponseDTO> pushSync(
            @RequestBody SyncPushRequestDTO request) {
        
        log.info("Push sync recebido do dispositivo: {} com {} itens",
                request.getDeviceId(), request.getItems().size());
        
        SyncPushResponseDTO response = syncService.processPushSync(request);
        
        log.info("Push sync processado: {} sucesso, {} conflito, {} erro",
                response.getSuccessCount(),
                response.getConflictCount(),
                response.getErrorCount());
        
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Pull: Buscar dados atualizados do servidor
     * 
     * Retorna registros que foram modificados após o timestamp "since"
     * permitindo que o cliente mantenha seu cache local atualizado.
     * 
     * GET /api/sync/pull?since=2026-05-29T10:00:00Z
     */
    @GetMapping("/sync/pull")
    @Operation(
        summary = "Buscar dados atualizados",
        description = "Retorna registros modificados no servidor desde o timestamp fornecido"
    )
    public ResponseEntity<SyncPullResponseDTO> pullSync(
            @RequestParam("since") String since) {
        
        log.info("Pull sync solicitado desde: {}", since);
        
        SyncPullResponseDTO response = syncService.performPullSync(since);
        
        log.info("Pull sync retornando {} itens", response.getItems().size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Status: Obter estado de sincronização de um dispositivo
     * 
     * Retorna informações sobre o último sync bem-sucedido e itens pendentes
     * para o dispositivo fornecido.
     * 
     * GET /api/sync/status/{deviceId}
     */
    @GetMapping("/sync/status/{deviceId}")
    @Operation(
        summary = "Obter status de sincronização",
        description = "Retorna o estado do último sync para um dispositivo específico"
    )
    public ResponseEntity<SyncStatusDTO> getSyncStatus(
            @PathVariable String deviceId) {
        
        log.debug("Status sync solicitado para dispositivo: {}", deviceId);
        
        SyncStatusDTO status = syncService.getSyncStatus(deviceId);
        
        return ResponseEntity.ok(status);
    }

    /**
     * Refresh: Renovar token JWT
     * 
     * Utilizado quando o token está vencendo ou vencido.
     * V3 (verificação de autenticação) do cliente usa este endpoint.
     * 
     * POST /api/auth/refresh
     * {
     *   "token": "jwt-token-atual"
     * }
     */
    @PostMapping("/auth/refresh")
    @Operation(summary = "Renovar token JWT", description = "Retorna um novo token JWT válido")
    public ResponseEntity<TokenRefreshResponse> refreshToken(
            @RequestBody TokenRefreshRequest request) {
        
        log.debug("Token refresh solicitado");
        
        // TODO: Implementar lógica de refresh
        // 1. Validar token atual
        // 2. Verificar se usuário ainda é válido
        // 3. Gerar novo token
        // 4. Retornar novo token
        
        return ResponseEntity.ok(new TokenRefreshResponse("novo-token"));
    }
}
