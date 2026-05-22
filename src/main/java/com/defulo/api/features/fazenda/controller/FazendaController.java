package com.defulo.api.features.fazenda.controller;

import com.defulo.api.features.fazenda.dto.request.FazendaCreateRequestDTO;
import com.defulo.api.features.fazenda.dto.request.FazendaUpdateRequestDTO;
import com.defulo.api.features.fazenda.dto.response.FazendaResponseDTO;
import com.defulo.api.features.fazenda.service.FazendaService;
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

import java.util.List;

/**
 * Controller REST para Fazenda.
 *
 * Endpoints:
 *  POST   /api/fazendas                           → criar
 *  GET    /api/fazendas                           → listar (paginado)
 *  GET    /api/fazendas/{id}                      → buscar por ID
 *  GET    /api/fazendas/por-produtor/{produtorId} → listar por produtor
 *  PUT    /api/fazendas/{id}                      → atualizar
 *  DELETE /api/fazendas/{id}                      → excluir
 */
@RestController
@RequestMapping("/api/fazendas")
@RequiredArgsConstructor
@Tag(name = "Fazendas", description = "Gestão de fazendas")
@SecurityRequirement(name = "bearerAuth")
public class FazendaController {

    private final FazendaService service;

    @PostMapping
    @Operation(summary = "Criar nova fazenda", description = "Cria uma nova fazenda associada a um produtor")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Fazenda criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<FazendaResponseDTO> criar(
            @RequestBody @Valid FazendaCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar fazendas", description = "Lista todas as fazendas com paginação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de fazendas"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Page<FazendaResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar fazenda por ID", description = "Retorna uma fazenda específica")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fazenda encontrada"),
            @ApiResponse(responseCode = "404", description = "Fazenda não encontrada")
    })
    public ResponseEntity<FazendaResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/por-produtor/{produtorId}")
    @Operation(
            summary = "Listar fazendas por produtor",
            description = "Lista todas as fazendas associadas a um produtor específico"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de fazendas do produtor"),
            @ApiResponse(responseCode = "404", description = "Produtor não encontrado")
    })
    public ResponseEntity<List<FazendaResponseDTO>> listarPorProdutor(
            @PathVariable Long produtorId) {
        return ResponseEntity.ok(service.listarPorProdutor(produtorId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar fazenda", description = "Atualiza dados da fazenda (nome, área, cultura)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fazenda atualizada"),
            @ApiResponse(responseCode = "404", description = "Fazenda não encontrada")
    })
    public ResponseEntity<FazendaResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid FazendaUpdateRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar fazenda", description = "Remove uma fazenda e todos seus talhões")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Fazenda deletada"),
            @ApiResponse(responseCode = "404", description = "Fazenda não encontrada")
    })
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
