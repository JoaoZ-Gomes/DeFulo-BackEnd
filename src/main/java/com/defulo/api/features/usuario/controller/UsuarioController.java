package com.defulo.api.features.usuario.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.defulo.api.features.usuario.dto.request.UsuarioCreateRequestDTO;
import com.defulo.api.features.usuario.dto.request.UsuarioUpdateRequestDTO;
import com.defulo.api.features.usuario.dto.response.UsuarioResponseDTO;
import com.defulo.api.features.usuario.service.UsuarioIService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para gestão de usuários genéricos.
 * Segue os padrões RESTful com status codes apropriados e paginação.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioIService service;

    // =====================================================
    // CREATE
    // =====================================================

    /**
     * Cria um novo usuário.
     * @param dto dados do usuário
     * @return ResponseEntity com 201 CREATED e o usuário criado
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@RequestBody @Valid UsuarioCreateRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.salvar(dto));
    }

    // =====================================================
    // READ
    // =====================================================

    /**
     * Lista usuários de forma paginada.
     * @param pageable parâmetros (page, size, sort)
     * @return Página de usuários
     */
    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.buscarTodos(pageable));
    }

    /**
     * Busca um usuário pelo ID.
     * @param id identificador
     * @return Dados do usuário
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**
     * Busca um usuário pelo email.
     * @param email endereço de email
     * @return Dados do usuário
     */
    @GetMapping("/por-email")
    public ResponseEntity<UsuarioResponseDTO> buscarPorEmail(@RequestParam String email) {
        return ResponseEntity.ok(service.buscarPorEmail(email));
    }

    // =====================================================
    // UPDATE
    // =====================================================

    /**
     * Atualiza dados de um usuário existente.
     * @param id identificador
     * @param dto dados para atualização
     * @return Usuário atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioUpdateRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    // =====================================================
    // DELETE
    // =====================================================

    /**
     * Remove um usuário pelo ID.
     * @param id identificador
     * @return 204 NO CONTENT
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluirPorId(id);
        return ResponseEntity.noContent().build();
    }
}