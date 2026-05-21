package com.defulo.api.features.evento.service;

import com.defulo.api.features.evento.dto.request.EventoCreateRequestDto;
import com.defulo.api.features.evento.dto.request.EventoUpdateDTO;
import com.defulo.api.features.evento.dto.response.EventoResponseDTO;
import com.defulo.api.features.evento.mapper.EventoMapper;
import com.defulo.api.features.evento.model.EventoManejo;
import com.defulo.api.features.evento.repository.EventoRepository;
import com.defulo.api.features.talhao.model.Talhao;
import com.defulo.api.features.talhao.repository.TalhaoRepository;
import com.defulo.api.infrastructure.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Serviço de Evento de Manejo com regras de negócio.
 *
 * Regras:
 *  - O talhão deve existir ao criar ou listar eventos.
 *  - A data de criação é definida automaticamente pelo sistema.
 *  - Apenas nome e descrição são mutáveis após criação.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EventoService {

    private final EventoRepository repository;
    private final EventoMapper mapper;
    private final TalhaoRepository talhaoRepository;

    // ─── CREATE ──────────────────────────────────────────────────────────────

    public EventoResponseDTO criar(EventoCreateRequestDto dto) {
        Talhao talhao = talhaoRepository.findById(dto.talhaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Talhão não encontrado com o ID: " + dto.talhaoId()));

        EventoManejo evento = mapper.toEntity(dto);
        evento.setData(LocalDateTime.now());
        evento.setTalhao(talhao);

        return mapper.toResponseDTO(repository.save(evento));
    }

    // ─── READ ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public EventoResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento não encontrado com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<EventoResponseDTO> listarPorTalhao(Long talhaoId, Pageable pageable) {
        if (!talhaoRepository.existsById(talhaoId)) {
            throw new RecursoNaoEncontradoException("Talhão não encontrado com o ID: " + talhaoId);
        }
        return repository.findByTalhaoId(talhaoId, pageable).map(mapper::toResponseDTO);
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    public EventoResponseDTO atualizar(Long id, EventoUpdateDTO dto) {
        EventoManejo evento = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento não encontrado com o ID: " + id));

        mapper.updateEntityFromDTO(dto, evento);
        return mapper.toResponseDTO(repository.save(evento));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Evento não encontrado com o ID: " + id);
        }
        repository.deleteById(id);
    }
}
