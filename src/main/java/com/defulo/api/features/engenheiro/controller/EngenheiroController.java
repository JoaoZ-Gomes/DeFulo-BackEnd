package com.defulo.api.features.engenheiro.controller;

import com.defulo.api.features.engenheiro.dto.request.EngenheiroCreateRequestDTO;
import com.defulo.api.features.engenheiro.dto.request.EngenheiroUpdateRequestDTO;
import com.defulo.api.features.engenheiro.dto.response.EngenheiroResponseDTO;
import com.defulo.api.features.engenheiro.service.IEngenheiroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para Engenheiro Agrônomo.
 * Endpoints: POST, GET (lista + por ID), PUT, DELETE.
 */
@RestController
@RequestMapping("/api/engenheiros")
@RequiredArgsConstructor
@Tag(name = "Engenheiros", description = "Gestão de Engenheiros Agrônomos")
public class EngenheiroController {

    private final IEngenheiroService service;

    @PostMapping
    @Operation(
            summary = "Criar novo engenheiro (onboarding)",
            description = "Cria um novo engenheiro. Endpoint aberto - não requer autenticação."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Engenheiro criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Email duplicado ou dados inválidos")
    })
    public ResponseEntity<EngenheiroResponseDTO> criar(
            @RequestBody @Valid EngenheiroCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar engenheiros", description = "Lista todos os engenheiros com paginação")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de engenheiros"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Page<EngenheiroResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.buscarTodos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar engenheiro por ID", description = "Retorna um engenheiro específico")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Engenheiro encontrado"),
            @ApiResponse(responseCode = "404", description = "Engenheiro não encontrado")
    })
    public ResponseEntity<EngenheiroResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar engenheiro", description = "Atualiza dados do engenheiro (nome, telefone, especialidade)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Engenheiro atualizado"),
            @ApiResponse(responseCode = "404", description = "Engenheiro não encontrado")
    })
    public ResponseEntity<EngenheiroResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid EngenheiroUpdateRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar engenheiro", description = "Remove um engenheiro do sistema")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Engenheiro deletado"),
            @ApiResponse(responseCode = "404", description = "Engenheiro não encontrado")
    })
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluirPorId(id);
        return ResponseEntity.noContent().build();
    }
}