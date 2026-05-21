package com.defulo.api.features.fazenda.service;

import com.defulo.api.features.fazenda.dto.request.FazendaCreateRequestDTO;
import com.defulo.api.features.fazenda.dto.request.FazendaUpdateRequestDTO;
import com.defulo.api.features.fazenda.dto.response.FazendaResponseDTO;
import com.defulo.api.features.fazenda.mapper.FazendaMapper;
import com.defulo.api.features.fazenda.model.Fazenda;
import com.defulo.api.features.fazenda.repository.FazendaRepository;
import com.defulo.api.features.produtor.model.Produtor;
import com.defulo.api.features.produtor.repository.ProdutorRepository;
import com.defulo.api.infrastructure.exception.RecursoNaoEncontradoException;
import com.defulo.api.infrastructure.exception.RegraDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço de Fazenda com regras de negócio.
 *
 * Regras:
 *  - O produtor deve existir ao criar uma fazenda.
 *  - O mesmo produtor não pode ter duas fazendas com o mesmo nome.
 *  - Apenas campos não-nulos são aplicados na atualização (partial update).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FazendaService {

    private final FazendaRepository fazendaRepository;
    private final ProdutorRepository produtorRepository;
    private final FazendaMapper mapper;

    // ─── CREATE ──────────────────────────────────────────────────────────────

    public FazendaResponseDTO criar(FazendaCreateRequestDTO dto) {
        Produtor produtor = produtorRepository.findById(dto.produtorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Produtor não encontrado com o ID: " + dto.produtorId()));

        if (fazendaRepository.existsByNomeAndProdutorId(dto.nome(), dto.produtorId())) {
            throw new RegraDeNegocioException(
                    "Este produtor já possui uma fazenda com o nome: '" + dto.nome() + "'.");
        }

        Fazenda fazenda = new Fazenda();
        fazenda.setNome(dto.nome());
        fazenda.setAreaTotal(dto.areaTotal());
        fazenda.setCultura(dto.cultura());
        fazenda.setProdutor(produtor);

        return mapper.toResponseDTO(fazendaRepository.save(fazenda));
    }

    // ─── READ ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<FazendaResponseDTO> listar(Pageable pageable) {
        return fazendaRepository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<FazendaResponseDTO> listarPorProdutor(Long produtorId) {
        if (!produtorRepository.existsById(produtorId)) {
            throw new RecursoNaoEncontradoException("Produtor não encontrado com o ID: " + produtorId);
        }
        return fazendaRepository.findByProdutorId(produtorId).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public FazendaResponseDTO buscarPorId(Long id) {
        return fazendaRepository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fazenda não encontrada com o ID: " + id));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    public FazendaResponseDTO atualizar(Long id, FazendaUpdateRequestDTO dto) {
        Fazenda fazenda = fazendaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fazenda não encontrada com o ID: " + id));

        if (dto.nome() != null) {
            boolean nomeConflito = fazendaRepository
                    .existsByNomeAndProdutorId(dto.nome(), fazenda.getProdutor().getId());
            if (nomeConflito && !dto.nome().equals(fazenda.getNome())) {
                throw new RegraDeNegocioException(
                        "Este produtor já possui outra fazenda com o nome: '" + dto.nome() + "'.");
            }
            fazenda.setNome(dto.nome());
        }
        if (dto.areaTotal() != null) fazenda.setAreaTotal(dto.areaTotal());
        if (dto.cultura()   != null) fazenda.setCultura(dto.cultura());

        return mapper.toResponseDTO(fazendaRepository.save(fazenda));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    public void excluir(Long id) {
        if (!fazendaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Fazenda não encontrada com o ID: " + id);
        }
        fazendaRepository.deleteById(id);
    }
}
