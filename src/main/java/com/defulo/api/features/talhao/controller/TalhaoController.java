package com.defulo.api.features.talhao.controller;

import com.defulo.api.features.talhao.dto.request.TalhaoCreateRequestDTO;
import com.defulo.api.features.talhao.dto.request.TalhaoUpdateRequestDTO;
import com.defulo.api.features.talhao.dto.response.TalhaoResponseDTO;
import com.defulo.api.features.talhao.service.TalhaoService;
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
public class TalhaoController {

    private final TalhaoService service;

    @PostMapping
    public ResponseEntity<TalhaoResponseDTO> criar(
            @RequestBody @Valid TalhaoCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TalhaoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/por-fazenda/{fazendaId}")
    public ResponseEntity<List<TalhaoResponseDTO>> listarPorFazenda(
            @PathVariable Long fazendaId) {
        return ResponseEntity.ok(service.listarPorFazenda(fazendaId));
    }

    @GetMapping("/paginado/{fazendaId}")
    public ResponseEntity<Page<TalhaoResponseDTO>> listarPorFazendaPaginado(
            @PathVariable Long fazendaId,
            @PageableDefault(size = 10, sort = "numero", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(service.listarPorFazendaPaginado(fazendaId, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TalhaoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid TalhaoUpdateRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
