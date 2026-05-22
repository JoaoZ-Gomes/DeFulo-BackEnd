package com.defulo.api.features.produtor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

import com.defulo.api.features.produtor.dto.request.ProdutorCreateRequestDTO;
import com.defulo.api.features.produtor.dto.request.ProdutorUpdateRequestDTO;
import com.defulo.api.features.produtor.dto.response.ProdutorResponseDTO;
import com.defulo.api.features.produtor.service.ProdutorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para Produtor.
 *
 * Endpoints:
 *  POST   /api/produtores      → criar (aberto, sem autenticação)
 *  GET    /api/produtores      → listar (paginado)
 *  GET    /api/produtores/{id} → buscar por ID
 *  PUT    /api/produtores/{id} → atualizar
 *  DELETE /api/produtores/{id} → excluir
 */
@RestController
@RequestMapping("/api/produtores")
@RequiredArgsConstructor
@Tag(name = "Produtores", description = "Gestão de produtores rurais")
public class ProdutorController {

    private final ProdutorService service;

    @PostMapping
    @Operation(
            summary = "Criar novo produtor (onboarding)",
            description = "Cria um novo produtor. Endpoint aberto - não requer autenticação."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produtor criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Email duplicado ou dados inválidos")
    })
    public ResponseEntity<ProdutorResponseDTO> criar(
            @RequestBody @Valid ProdutorCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar produtores", description = "Lista todos os produtores com paginação")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de produtores"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Page<ProdutorResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.buscarTodos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produtor por ID", description = "Retorna um produtor específico")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produtor encontrado"),
            @ApiResponse(responseCode = "404", description = "Produtor não encontrado")
    })
    public ResponseEntity<ProdutorResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produtor", description = "Atualiza dados do produtor (nome, telefone, propriedade, área)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produtor atualizado"),
            @ApiResponse(responseCode = "404", description = "Produtor não encontrado")
    })
    public ResponseEntity<ProdutorResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ProdutorUpdateRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar produtor", description = "Remove um produtor e todos seus recursos associados")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produtor deletado"),
            @ApiResponse(responseCode = "404", description = "Produtor não encontrado")
    })
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluirPorId(id);
        return ResponseEntity.noContent().build();
    }
}
