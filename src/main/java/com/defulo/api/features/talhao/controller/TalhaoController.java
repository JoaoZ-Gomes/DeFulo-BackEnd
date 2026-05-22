package com.defulo.api.features.talhao.controller;

import com.defulo.api.features.talhao.dto.request.TalhaoCreateRequestDTO;
import com.defulo.api.features.talhao.dto.request.TalhaoUpdateRequestDTO;
import com.defulo.api.features.talhao.dto.response.TalhaoResponseDTO;
import com.defulo.api.features.talhao.service.TalhaoService;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para Talhão.
 *
 * Endpoints:
 *  POST   /api/talhoes                           → criar
 *  GET    /api/talhoes/{id}                      → buscar por ID
 *  GET    /api/talhoes/por-fazenda/{fazendaId}   → listar por fazenda (lista simples)
 *  GET    /api/talhoes/paginado/{fazendaId}      → listar por fazenda (paginado)
 *  PUT    /api/talhoes/{id}                      → atualizar
 *  DELETE /api/talhoes/{id}                      → excluir
 */
@RestController
@RequestMapping("/api/talhoes")
@RequiredArgsConstructor
@Tag(name = "Talhões", description = "Gestão de talhões (unidades técnicas)")
@SecurityRequirement(name = "bearerAuth")
public class TalhaoController {

    private final TalhaoService service;

    @PostMapping
    @Operation(summary = "Criar novo talhão", description = "Cria um novo talhão associado a uma fazenda")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Talhão criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou fazenda não encontrada")
    })
    public ResponseEntity<TalhaoResponseDTO> criar(
            @RequestBody @Valid TalhaoCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar talhão por ID", description = "Retorna um talhão específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Talhão encontrado"),
            @ApiResponse(responseCode = "404", description = "Talhão não encontrado")
    })
    public ResponseEntity<TalhaoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/por-fazenda/{fazendaId}")
    @Operation(
            summary = "Listar talhões por fazenda (lista simples)",
            description = "Lista todos os talhões de uma fazenda (sem paginação)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de talhões"),
            @ApiResponse(responseCode = "404", description = "Fazenda não encontrada")
    })
    public ResponseEntity<List<TalhaoResponseDTO>> listarPorFazenda(
            @PathVariable Long fazendaId) {
        return ResponseEntity.ok(service.listarPorFazenda(fazendaId));
    }

    @GetMapping("/paginado/{fazendaId}")
    @Operation(
            summary = "Listar talhões por fazenda (paginado)",
            description = "Lista todos os talhões de uma fazenda com suporte a paginação e ordenação"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de talhões"),
            @ApiResponse(responseCode = "404", description = "Fazenda não encontrada")
    })
    public ResponseEntity<Page<TalhaoResponseDTO>> listarPorFazendaPaginado(
            @PathVariable Long fazendaId,
            @PageableDefault(size = 10, sort = "numero", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(service.listarPorFazendaPaginado(fazendaId, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar talhão", description = "Atualiza dados do talhão (número, área, cultura, etc)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Talhão atualizado"),
            @ApiResponse(responseCode = "404", description = "Talhão não encontrado")
    })
    public ResponseEntity<TalhaoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid TalhaoUpdateRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar talhão", description = "Remove um talhão e todos seus eventos associados")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Talhão deletado"),
            @ApiResponse(responseCode = "404", description = "Talhão não encontrado")
    })
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
