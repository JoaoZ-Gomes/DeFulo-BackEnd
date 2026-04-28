package com.defulo.api.features.engenheiro.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.defulo.api.features.engenheiro.dto.request.EngenheiroCreateRequestDTO;
import com.defulo.api.features.engenheiro.dto.response.EngenheiroResponseDTO;
import com.defulo.api.features.engenheiro.service.IEngenheiroService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/engenheiros")
@RequiredArgsConstructor
public class EngenheiroController {

    private final IEngenheiroService service;

    @PostMapping
    public ResponseEntity<EngenheiroResponseDTO> criar(@RequestBody @Valid EngenheiroCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @GetMapping
    public ResponseEntity<Page<EngenheiroResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.buscarTodos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EngenheiroResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}