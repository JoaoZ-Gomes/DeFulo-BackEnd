package com.defulo.api.infrastructure.sync.controller;

import com.defulo.api.features.usuario.model.Usuario;
import com.defulo.api.features.usuario.repository.UsuarioRepository;
import com.defulo.api.infrastructure.exception.InvalidTokenException;
import com.defulo.api.infrastructure.security.TokenService;
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
 * Controller para endpoints de sincronização offline-first e renovação de token.
 *
 * Endpoints:
 *  GET  /api/health          — verificação de conectividade
 *  POST /api/sync/push       — envio de operações offline em lote
 *  GET  /api/sync/pull       — busca de dados modificados (incremental)
 *  GET  /api/sync/status/:id — estado do último sync de um dispositivo
 *  POST /api/auth/refresh    — renovação do token JWT
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Sincronização", description = "Endpoints para sincronização offline-first e renovação de token")
public class SyncController {

    private final SyncService syncService;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    // ─── HEALTH ────────────────────────────────────────────────────────────────

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

    // ─── PUSH ──────────────────────────────────────────────────────────────────

    @PostMapping("/sync/push")
    @Operation(
        summary = "Sincronizar operações do cliente",
        description = "Recebe lote de operações (CREATE/UPDATE/DELETE) para processar no servidor de forma idempotente"
    )
    public ResponseEntity<SyncPushResponseDTO> pushSync(@RequestBody SyncPushRequestDTO request) {
        log.info("Push sync recebido | dispositivo={} | {} itens",
                request.getDeviceId(), request.getItems().size());

        SyncPushResponseDTO response = syncService.processPushSync(request);

        log.info("Push sync concluído | sucesso={} conflito={} erro={}",
                response.getSuccessCount(), response.getConflictCount(), response.getErrorCount());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // ─── PULL ──────────────────────────────────────────────────────────────────

    @GetMapping("/sync/pull")
    @Operation(
        summary = "Buscar dados atualizados",
        description = "Retorna registros modificados no servidor desde o timestamp fornecido (ISO 8601)"
    )
    public ResponseEntity<SyncPullResponseDTO> pullSync(@RequestParam("since") String since) {
        log.info("Pull sync solicitado | desde={}", since);

        SyncPullResponseDTO response = syncService.performPullSync(since);

        log.info("Pull sync concluído | {} itens retornados", response.getItems().size());
        return ResponseEntity.ok(response);
    }

    // ─── STATUS ────────────────────────────────────────────────────────────────

    @GetMapping("/sync/status/{deviceId}")
    @Operation(
        summary = "Obter status de sincronização",
        description = "Retorna o estado do último sync para um dispositivo específico"
    )
    public ResponseEntity<SyncStatusDTO> getSyncStatus(@PathVariable String deviceId) {
        log.debug("Status sync | deviceId={}", deviceId);
        return ResponseEntity.ok(syncService.getSyncStatus(deviceId));
    }

    // ─── REFRESH TOKEN ─────────────────────────────────────────────────────────

    @PostMapping("/auth/refresh")
    @Operation(
        summary = "Renovar token JWT",
        description = "Valida o token atual e emite um novo token JWT com prazo renovado"
    )
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        log.debug("Token refresh solicitado");

        // 1. Extrair o subject (email) do token atual — lança InvalidTokenException se inválido
        String email;
        try {
            email = tokenService.getSubject(request.getToken());
        } catch (InvalidTokenException ex) {
            log.warn("Refresh rejeitado — token inválido ou expirado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. Verificar se o usuário ainda existe e está ativo
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElse(null);

        if (usuario == null || !usuario.isEnabled()) {
            log.warn("Refresh rejeitado — usuário '{}' não encontrado ou inativo", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 3. Gerar novo token e retornar
        String novoToken = tokenService.gerarToken(usuario);
        log.info("Token renovado para usuário: {}", email);

        return ResponseEntity.ok(new TokenRefreshResponse(novoToken));
    }
}
