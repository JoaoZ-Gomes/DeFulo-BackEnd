package com.defulo.api.features.evento.controller;

import com.defulo.api.features.evento.dto.request.EventoCreateRequestDto;
import com.defulo.api.features.evento.dto.request.EventoUpdateDTO;
import com.defulo.api.features.evento.dto.response.EventoResponseDTO;
import com.defulo.api.features.evento.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para Eventos de Manejo.
 *
 * Endpoints:
 *  POST   /api/eventos                          → criar
 *  GET    /api/eventos/{id}                     → buscar por ID
 *  GET    /api/eventos/por-talhao/{talhaoId}    → listar por talhão (paginado, desc data)
 *  PUT    /api/eventos/{id}                     → atualizar (nome e descrição)
 *  DELETE /api/eventos/{id}                     → excluir
 */
@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService service;

    @PostMapping
    public ResponseEntity<EventoResponseDTO> criar(
            @RequestBody @Valid EventoCreateRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/por-talhao/{talhaoId}")
    public ResponseEntity<Page<EventoResponseDTO>> listarPorTalhao(
            @PathVariable Long talhaoId,
            @PageableDefault(size = 10, sort = "data", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.listarPorTalhao(talhaoId, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid EventoUpdateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
