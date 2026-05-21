package com.defulo.api.features.fazenda.controller;

import com.defulo.api.features.fazenda.dto.request.FazendaCreateRequestDTO;
import com.defulo.api.features.fazenda.dto.request.FazendaUpdateRequestDTO;
import com.defulo.api.features.fazenda.dto.response.FazendaResponseDTO;
import com.defulo.api.features.fazenda.service.FazendaService;
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
public class FazendaController {

    private final FazendaService service;

    @PostMapping
    public ResponseEntity<FazendaResponseDTO> criar(
            @RequestBody @Valid FazendaCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<Page<FazendaResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FazendaResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/por-produtor/{produtorId}")
    public ResponseEntity<List<FazendaResponseDTO>> listarPorProdutor(
            @PathVariable Long produtorId) {
        return ResponseEntity.ok(service.listarPorProdutor(produtorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FazendaResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid FazendaUpdateRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
