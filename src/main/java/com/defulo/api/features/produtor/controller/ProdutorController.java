package com.defulo.api.features.produtor.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.defulo.api.features.produtor.dto.request.ProdutorCreateRequestDTO;
import com.defulo.api.features.produtor.dto.response.ProdutorResponseDTO;
import com.defulo.api.features.produtor.service.ProdutorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/produtores")
@RequiredArgsConstructor
public class ProdutorController {

    private final ProdutorService service;

    @PostMapping
    public ResponseEntity<ProdutorResponseDTO> criar(@RequestBody @Valid ProdutorCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @GetMapping
    public ResponseEntity<Page<ProdutorResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.buscarTodos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutorResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
