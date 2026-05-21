package com.defulo.api.features.usuario.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.defulo.api.features.usuario.dto.request.UsuarioCreateRequestDTO;
import com.defulo.api.features.usuario.dto.request.UsuarioUpdateRequestDTO;
import com.defulo.api.features.usuario.dto.response.UsuarioResponseDTO;
import com.defulo.api.features.usuario.mapper.UsuarioMapper;
import com.defulo.api.features.usuario.model.Usuario;
import com.defulo.api.features.usuario.repository.UsuarioRepository;
import com.defulo.api.infrastructure.exception.RecursoNaoEncontradoException;
import com.defulo.api.infrastructure.exception.RegraDeNegocioException;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

/**
 * Implementação do serviço de Usuário.
 * Contém a lógica de negócio principal e orquestração entre repositório e mappers.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService implements UsuarioIService {

    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponseDTO salvar(UsuarioCreateRequestDTO dto) {
        if (repository.existsByEmail(dto.email())) {
            throw new RegraDeNegocioException("Este email já está cadastrado no sistema.");
        }

        if (dto.cpf() != null && repository.existsByCpf(dto.cpf())) {
            throw new RegraDeNegocioException("Este CPF já está cadastrado no sistema.");
        }

        Usuario entity = mapper.toEntity(dto);
        entity.setSenha(passwordEncoder.encode(dto.senha()));

        return mapper.toResponseDTO(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com o ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> buscarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorEmail(String email) {
        return repository.findByEmail(email)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com o email: " + email));
    }

    @Override
    public UsuarioResponseDTO atualizar(Long id, UsuarioUpdateRequestDTO dto) {
        Usuario entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com o ID: " + id));

        mapper.updateEntityFromDTO(dto, entity);

        return mapper.toResponseDTO(repository.save(entity));
    }

    @Override
    public void excluirPorId(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Não foi possível excluir. Usuário não encontrado com o ID: " + id);
        }
        repository.deleteById(id);
    }
}