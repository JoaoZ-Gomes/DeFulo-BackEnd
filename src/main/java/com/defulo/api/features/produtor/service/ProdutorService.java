package com.defulo.api.features.produtor.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.defulo.api.features.produtor.dto.request.ProdutorCreateRequestDTO;
import com.defulo.api.features.produtor.dto.request.ProdutorUpdateRequestDTO;
import com.defulo.api.features.produtor.dto.response.ProdutorResponseDTO;
import com.defulo.api.features.produtor.mapper.ProdutorMapper;
import com.defulo.api.features.produtor.model.Produtor;
import com.defulo.api.features.produtor.repository.ProdutorRepository;
import com.defulo.api.features.usuario.repository.UsuarioRepository;
import com.defulo.api.infrastructure.exception.RecursoNaoEncontradoException;
import com.defulo.api.infrastructure.exception.RegraDeNegocioException;

import lombok.RequiredArgsConstructor;

/**
 * Serviço de Produtor com regras de negócio.
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
public class ProdutorService {

    private final ProdutorRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutorMapper mapper;
    private final PasswordEncoder passwordEncoder;

    // ─── CREATE ──────────────────────────────────────────────────────────────

    public ProdutorResponseDTO salvar(ProdutorCreateRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RegraDeNegocioException("Este email já está cadastrado no sistema.");
        }
        if (dto.cpf() != null && usuarioRepository.existsByCpf(dto.cpf())) {
            throw new RegraDeNegocioException("Este CPF já está cadastrado no sistema.");
        }

        Produtor entity = mapper.toEntity(dto);
        entity.setSenha(passwordEncoder.encode(dto.senha()));

        return mapper.toResponseDTO(repository.save(entity));
    }

    // ─── READ ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ProdutorResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produtor não encontrado com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<ProdutorResponseDTO> buscarTodos(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    public ProdutorResponseDTO atualizar(Long id, ProdutorUpdateRequestDTO dto) {
        Produtor produtor = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produtor não encontrado com o ID: " + id));

        if (dto.nome()        != null) produtor.setNome(dto.nome());
        if (dto.telefone()    != null) produtor.setTelefone(dto.telefone());
        if (dto.propriedade() != null) produtor.setPropriedade(dto.propriedade());
        if (dto.areaTotal()   != null) produtor.setAreaTotal(dto.areaTotal());

        return mapper.toResponseDTO(repository.save(produtor));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    public void excluirPorId(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Produtor não encontrado com o ID: " + id);
        }
        repository.deleteById(id);
    }
}
