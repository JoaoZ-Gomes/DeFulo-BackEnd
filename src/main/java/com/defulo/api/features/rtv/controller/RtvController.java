package com.defulo.api.features.rtv.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.defulo.api.features.rtv.dto.request.RtvCreateRequestDTO;
import com.defulo.api.features.rtv.dto.response.RtvResponseDTO;
import com.defulo.api.features.rtv.service.RtvService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rtvs")
@RequiredArgsConstructor
public class RtvController {

    private final RtvService service;

    @PostMapping
    public ResponseEntity<RtvResponseDTO> criar(@RequestBody @Valid RtvCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @GetMapping
    public ResponseEntity<Page<RtvResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.buscarTodos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RtvResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
