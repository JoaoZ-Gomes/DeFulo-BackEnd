package com.defulo.api.features.engenheiro.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.defulo.api.features.engenheiro.dto.request.EngenheiroCreateRequestDTO;
import com.defulo.api.features.engenheiro.dto.response.EngenheiroResponseDTO;
import com.defulo.api.features.engenheiro.mapper.EngenheiroMapper;
import com.defulo.api.features.engenheiro.model.Engenheiro;
import com.defulo.api.features.engenheiro.repository.EngenheiroRepository;
import com.defulo.api.features.usuario.repository.UsuarioRepository;
import com.defulo.api.infrastructure.exception.RecursoNaoEncontradoException;
import com.defulo.api.infrastructure.exception.RegraDeNegocioException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EngenheiroService implements IEngenheiroService {

    private final EngenheiroRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final EngenheiroMapper mapper;

    @Override
    public EngenheiroResponseDTO salvar(EngenheiroCreateRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RegraDeNegocioException("Este email já está cadastrado.");
        }

        Engenheiro entity = mapper.toEntity(dto);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public EngenheiroResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Engenheiro não encontrado: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EngenheiroResponseDTO> buscarTodos(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }
}