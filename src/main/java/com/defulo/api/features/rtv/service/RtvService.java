package com.defulo.api.features.rtv.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.defulo.api.features.rtv.dto.request.RtvCreateRequestDTO;
import com.defulo.api.features.rtv.dto.request.RtvUpdateRequestDTO;
import com.defulo.api.features.rtv.dto.response.RtvResponseDTO;
import com.defulo.api.features.rtv.mapper.RtvMapper;
import com.defulo.api.features.rtv.model.Rtv;
import com.defulo.api.features.rtv.repository.RtvRepository;
import com.defulo.api.features.usuario.repository.UsuarioRepository;
import com.defulo.api.infrastructure.exception.RecursoNaoEncontradoException;
import com.defulo.api.infrastructure.exception.RegraDeNegocioException;

import lombok.RequiredArgsConstructor;

/**
 * Serviço de RTV com regras de negócio.
 *
 * Regras:
 *  - Email deve ser único no sistema.
 *  - CPF deve ser único se informado.
 *  - Senha armazenada com hash BCrypt.
 *  - Email, CPF e senha não são alteráveis via endpoint de atualização.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RtvService {

    private final RtvRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final RtvMapper mapper;
    private final PasswordEncoder passwordEncoder;

    // ─── CREATE ──────────────────────────────────────────────────────────────

    public RtvResponseDTO salvar(RtvCreateRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RegraDeNegocioException("Este email já está cadastrado no sistema.");
        }
        if (dto.cpf() != null && usuarioRepository.existsByCpf(dto.cpf())) {
            throw new RegraDeNegocioException("Este CPF já está cadastrado no sistema.");
        }

        Rtv entity = mapper.toEntity(dto);
        entity.setSenha(passwordEncoder.encode(dto.senha()));

        return mapper.toResponseDTO(repository.save(entity));
    }

    // ─── READ ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public RtvResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("RTV não encontrado com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<RtvResponseDTO> buscarTodos(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    public RtvResponseDTO atualizar(Long id, RtvUpdateRequestDTO dto) {
        Rtv rtv = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("RTV não encontrado com o ID: " + id));

        if (dto.nome()       != null) rtv.setNome(dto.nome());
        if (dto.telefone()   != null) rtv.setTelefone(dto.telefone());
        if (dto.regiao()     != null) rtv.setRegiao(dto.regiao());
        if (dto.codigoRtv()  != null) rtv.setCodigoRtv(dto.codigoRtv());

        return mapper.toResponseDTO(repository.save(rtv));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    public void excluirPorId(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("RTV não encontrado com o ID: " + id);
        }
        repository.deleteById(id);
    }
}
