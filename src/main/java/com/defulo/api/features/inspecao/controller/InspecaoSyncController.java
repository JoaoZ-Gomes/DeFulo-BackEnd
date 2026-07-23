package com.defulo.api.features.inspecao.controller;

import com.defulo.api.features.inspecao.dto.request.InspecaoSyncRequestDTO;
import com.defulo.api.features.inspecao.dto.response.InspecaoSyncResponseDTO;
import com.defulo.api.features.inspecao.model.InspecaoCampo;
import com.defulo.api.features.inspecao.service.InspecaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para recebimento de dados e controle de sincronização
 * de Inspeções Fitossanitárias e Laudos de Monitoramento.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Inspeções de Campo", description = "Monitoramento Fitossanitário e Sincronização Local-First")
@SecurityRequirement(name = "bearerAuth")
public class InspecaoSyncController {

    private final InspecaoService service;

    @PostMapping("/sync/inspecoes")
    @Operation(
            summary = "Sincronizar lote de inspeções fitossanitárias",
            description = "Recebe um lote de laudos de inspeção técnica coletados offline pelo Engenheiro Agrônomo. " +
                    "Garante idempotência através do localId fornecido para cada item."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lote processado (respostas individuais indicam sucesso ou falha por item)")
    })
    public ResponseEntity<List<InspecaoSyncResponseDTO>> sincronizarLote(
            @RequestBody @Valid List<InspecaoSyncRequestDTO> dtos,
            @RequestHeader(value = "X-Device-ID", required = false, defaultValue = "unknown") String deviceId
    ) {
        return ResponseEntity.ok(service.sincronizarLote(dtos, deviceId));
    }

    @GetMapping("/inspecoes/por-talhao/{talhaoId}")
    @Operation(
            summary = "Listar laudos de monitoramento por talhão",
            description = "Retorna os laudos de inspeção técnica fitossanitária de um talhão específico ordenados por data desc."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada de laudos"),
            @ApiResponse(responseCode = "404", description = "Talhão não encontrado ou acesso negado")
    })
    public ResponseEntity<Page<InspecaoCampo>> listarPorTalhao(
            @PathVariable Long talhaoId,
            @PageableDefault(size = 10, sort = "dataInspecao", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.listarPorTalhao(talhaoId, pageable));
    }
}
