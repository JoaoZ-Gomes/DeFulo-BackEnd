package com.defulo.api.features.evento.controller;

import com.defulo.api.features.evento.dto.request.EventoCreateRequestDto;
import com.defulo.api.features.evento.dto.request.EventoUpdateDTO;
import com.defulo.api.features.evento.dto.response.EventoResponseDTO;
import com.defulo.api.features.evento.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Eventos de Manejo", description = "Gestão de eventos de manejo em talhões")
@SecurityRequirement(name = "bearerAuth")
public class EventoController {

    private final EventoService service;

    @PostMapping
    @Operation(
            summary = "Criar novo evento de manejo",
            description = "Registra um novo evento de manejo (irrigação, adubação, aplicação, etc) em um talhão. " +
                    "A data é preenchida automaticamente com o momento atual."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou talhão não encontrado")
    })
    public ResponseEntity<EventoResponseDTO> criar(
            @RequestBody @Valid EventoCreateRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar evento por ID", description = "Retorna um evento específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento encontrado"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    public ResponseEntity<EventoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/por-talhao/{talhaoId}")
    @Operation(
            summary = "Listar eventos por talhão",
            description = "Lista todos os eventos de um talhão ordenados por data (mais recentes primeiro) com paginação"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de eventos"),
            @ApiResponse(responseCode = "404", description = "Talhão não encontrado")
    })
    public ResponseEntity<Page<EventoResponseDTO>> listarPorTalhao(
            @PathVariable Long talhaoId,
            @PageableDefault(size = 10, sort = "data", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.listarPorTalhao(talhaoId, pageable));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar evento",
            description = "Atualiza apenas nome e descrição do evento. " +
                    "Tipo, quantidade, data e referências não são mutáveis."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento atualizado"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    public ResponseEntity<EventoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid EventoUpdateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar evento", description = "Remove um evento de manejo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Evento deletado"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
