package com.defulo.api.features.engenheiro.service;

import com.defulo.api.features.engenheiro.dto.request.EngenheiroCreateRequestDTO;
import com.defulo.api.features.engenheiro.dto.request.EngenheiroUpdateRequestDTO;
import com.defulo.api.features.engenheiro.dto.response.EngenheiroResponseDTO;
import com.defulo.api.features.engenheiro.mapper.EngenheiroMapper;
import com.defulo.api.features.engenheiro.model.Engenheiro;
import com.defulo.api.features.engenheiro.repository.EngenheiroRepository;
import com.defulo.api.features.usuario.repository.UsuarioRepository;
import com.defulo.api.infrastructure.exception.RecursoNaoEncontradoException;
import com.defulo.api.infrastructure.exception.RegraDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de Engenheiro Agrônomo com regras de negócio.
 *
 * Regras:
 *  - Email deve ser único no sistema (verificado na tabela usuarios).
 *  - CPF deve ser único se informado.
 *  - Senha é armazenada com hash BCrypt.
 *  - Email, CPF e senha não são alteráveis via endpoint de atualização.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EngenheiroService implements IEngenheiroService {

    private final EngenheiroRepository engenheiroRepository;
    private final UsuarioRepository usuarioRepository;
    private final EngenheiroMapper mapper;
    private final PasswordEncoder passwordEncoder;

    // ─── CREATE ──────────────────────────────────────────────────────────────

    @Override
    public EngenheiroResponseDTO salvar(EngenheiroCreateRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RegraDeNegocioException("Este email já está cadastrado no sistema.");
        }
        if (dto.cpf() != null && usuarioRepository.existsByCpf(dto.cpf())) {
            throw new RegraDeNegocioException("Este CPF já está cadastrado no sistema.");
        }

        Engenheiro engenheiro = mapper.toEntity(dto);
        engenheiro.setSenha(passwordEncoder.encode(dto.senha()));

        return mapper.toResponseDTO(engenheiroRepository.save(engenheiro));
    }

    // ─── READ ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public EngenheiroResponseDTO buscarPorId(Long id) {
        return engenheiroRepository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Engenheiro não encontrado com o ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EngenheiroResponseDTO> buscarTodos(Pageable pageable) {
        return engenheiroRepository.findAll(pageable).map(mapper::toResponseDTO);
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    @Override
    public EngenheiroResponseDTO atualizar(Long id, EngenheiroUpdateRequestDTO dto) {
        Engenheiro engenheiro = engenheiroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Engenheiro não encontrado com o ID: " + id));

        mapper.updateEntityFromDTO(dto, engenheiro);
        return mapper.toResponseDTO(engenheiroRepository.save(engenheiro));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @Override
    public void excluirPorId(Long id) {
        if (!engenheiroRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Engenheiro não encontrado com o ID: " + id);
        }
        engenheiroRepository.deleteById(id);
    }
}