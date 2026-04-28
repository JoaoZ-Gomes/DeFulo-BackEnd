package com.defulo.api.features.rtv.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.defulo.api.features.rtv.dto.request.RtvCreateRequestDTO;
import com.defulo.api.features.rtv.dto.response.RtvResponseDTO;
import com.defulo.api.features.rtv.mapper.RtvMapper;
import com.defulo.api.features.rtv.model.Rtv;
import com.defulo.api.features.rtv.repository.RtvRepository;
import com.defulo.api.features.usuario.repository.UsuarioRepository;
import com.defulo.api.infrastructure.exception.RecursoNaoEncontradoException;
import com.defulo.api.infrastructure.exception.RegraDeNegocioException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RtvService {

    private final RtvRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final RtvMapper mapper;

    public RtvResponseDTO salvar(RtvCreateRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RegraDeNegocioException("Este email já está cadastrado.");
        }

        Rtv entity = mapper.toEntity(dto);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public RtvResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("RTV não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Page<RtvResponseDTO> buscarTodos(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }
}
