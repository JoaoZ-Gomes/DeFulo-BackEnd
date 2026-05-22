package com.defulo.api.features.rtv.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.defulo.api.features.rtv.dto.request.RtvCreateRequestDTO;
import com.defulo.api.features.rtv.dto.request.RtvUpdateRequestDTO;
import com.defulo.api.features.rtv.dto.response.RtvResponseDTO;
import com.defulo.api.features.rtv.service.RtvService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para RTV (Representante Técnico de Vendas).
 *
 * Endpoints:
 *  POST   /api/rtvs      → criar (aberto, sem autenticação)
 *  GET    /api/rtvs      → listar (paginado)
 *  GET    /api/rtvs/{id} → buscar por ID
 *  PUT    /api/rtvs/{id} → atualizar
 *  DELETE /api/rtvs/{id} → excluir
 */
@RestController
@RequestMapping("/api/rtvs")
@RequiredArgsConstructor
@Tag(name = "RTVs", description = "Gestão de Representantes Técnicos de Vendas")
public class RtvController {

    private final RtvService service;

    @PostMapping
    @Operation(
            summary = "Criar novo RTV (onboarding)",
            description = "Cria um novo RTV. Endpoint aberto - não requer autenticação."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "RTV criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Email duplicado ou dados inválidos")
    })
    public ResponseEntity<RtvResponseDTO> criar(
            @RequestBody @Valid RtvCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar RTVs", description = "Lista todos os RTVs com paginação")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de RTVs"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Page<RtvResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.buscarTodos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar RTV por ID", description = "Retorna um RTV específico")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "RTV encontrado"),
            @ApiResponse(responseCode = "404", description = "RTV não encontrado")
    })
    public ResponseEntity<RtvResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar RTV", description = "Atualiza dados do RTV (nome, telefone, região, código)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "RTV atualizado"),
            @ApiResponse(responseCode = "404", description = "RTV não encontrado")
    })
    public ResponseEntity<RtvResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid RtvUpdateRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar RTV", description = "Remove um RTV do sistema")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "RTV deletado"),
            @ApiResponse(responseCode = "404", description = "RTV não encontrado")
    })
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluirPorId(id);
        return ResponseEntity.noContent().build();
    }
}
