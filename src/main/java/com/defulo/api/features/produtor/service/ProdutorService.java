package com.defulo.api.features.produtor.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.defulo.api.features.produtor.dto.request.ProdutorCreateRequestDTO;
import com.defulo.api.features.produtor.dto.response.ProdutorResponseDTO;
import com.defulo.api.features.produtor.mapper.ProdutorMapper;
import com.defulo.api.features.produtor.model.Produtor;
import com.defulo.api.features.produtor.repository.ProdutorRepository;
import com.defulo.api.features.usuario.repository.UsuarioRepository;
import com.defulo.api.infrastructure.exception.RecursoNaoEncontradoException;
import com.defulo.api.infrastructure.exception.RegraDeNegocioException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProdutorService {

    private final ProdutorRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutorMapper mapper;

    public ProdutorResponseDTO salvar(ProdutorCreateRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RegraDeNegocioException("Este email já está cadastrado.");
        }

        Produtor entity = mapper.toEntity(dto);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public ProdutorResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produtor não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Page<ProdutorResponseDTO> buscarTodos(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }
}
