package com.defulo.api.features.evento.service;

import com.defulo.api.features.evento.dto.request.EventoCreateRequestDto;
import com.defulo.api.features.evento.dto.request.EventoUpdateDTO;
import com.defulo.api.features.evento.dto.response.EventoResponseDTO;
import com.defulo.api.features.evento.mapper.EventoMapper;
import com.defulo.api.features.evento.model.EventoManejo;
import com.defulo.api.features.evento.repository.EventoRepository;
import com.defulo.api.features.rtv.model.Rtv;
import com.defulo.api.features.rtv.repository.RtvRepository;
import com.defulo.api.features.talhao.model.Talhao;
import com.defulo.api.features.talhao.repository.TalhaoRepository;
import com.defulo.api.features.usuario.model.Perfil;
import com.defulo.api.features.usuario.model.Usuario;
import com.defulo.api.infrastructure.exception.RecursoNaoEncontradoException;
import com.defulo.api.infrastructure.security.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class EventoService {

    private final EventoRepository repository;
    private final EventoMapper mapper;
    private final TalhaoRepository talhaoRepository;
    private final RtvRepository rtvRepository;
    private final AuthorizationService authorizationService;

    public EventoResponseDTO criar(EventoCreateRequestDto dto) {
        Talhao talhao = buscarTalhao(dto.talhaoId());
        authorizationService.exigirAcessoATalhao(talhao);

        EventoManejo evento = mapper.toEntity(dto);
        evento.setData(LocalDateTime.now());
        evento.setTalhao(talhao);
        vincularRtvAutenticado(evento);

        return mapper.toResponseDTO(repository.save(evento));
    }

    @Transactional(readOnly = true)
    public EventoResponseDTO buscarPorId(Long id) {
        EventoManejo evento = buscarEntidade(id);
        authorizationService.exigirAcessoAEvento(evento);
        return mapper.toResponseDTO(evento);
    }

    @Transactional(readOnly = true)
    public Page<EventoResponseDTO> listarPorTalhao(Long talhaoId, Pageable pageable) {
        Talhao talhao = buscarTalhao(talhaoId);
        authorizationService.exigirAcessoATalhao(talhao);

        return repository.findByTalhaoId(talhaoId, pageable).map(mapper::toResponseDTO);
    }

    public EventoResponseDTO atualizar(Long id, EventoUpdateDTO dto) {
        EventoManejo evento = buscarEntidade(id);
        authorizationService.exigirAcessoAEvento(evento);

        mapper.updateEntityFromDTO(dto, evento);
        return mapper.toResponseDTO(repository.save(evento));
    }

    public void excluir(Long id) {
        EventoManejo evento = buscarEntidade(id);
        authorizationService.exigirAcessoAEvento(evento);
        repository.delete(evento);
    }

    private Talhao buscarTalhao(Long id) {
        return talhaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Talhao nao encontrado com o ID: " + id));
    }

    private EventoManejo buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento nao encontrado com o ID: " + id));
    }

    private void vincularRtvAutenticado(EventoManejo evento) {
        Usuario usuario = authorizationService.getUsuarioAutenticado();
        if (usuario.getPerfil() != Perfil.RTV) {
            return;
        }
        Rtv rtv = rtvRepository.findById(usuario.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("RTV nao encontrado com o ID: " + usuario.getId()));
        evento.setRtv(rtv);
    }
}
