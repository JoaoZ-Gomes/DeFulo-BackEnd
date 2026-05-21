package com.defulo.api.features.talhao.service;

import com.defulo.api.features.fazenda.model.Fazenda;
import com.defulo.api.features.fazenda.repository.FazendaRepository;
import com.defulo.api.features.talhao.dto.request.TalhaoCreateRequestDTO;
import com.defulo.api.features.talhao.dto.request.TalhaoUpdateRequestDTO;
import com.defulo.api.features.talhao.dto.response.TalhaoResponseDTO;
import com.defulo.api.features.talhao.mapper.TalhaoMapper;
import com.defulo.api.features.talhao.model.Talhao;
import com.defulo.api.features.talhao.repository.TalhaoRepository;
import com.defulo.api.infrastructure.exception.RecursoNaoEncontradoException;
import com.defulo.api.infrastructure.exception.RegraDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço de Talhão com regras de negócio.
 *
 * Regras:
 *  - A fazenda deve existir ao criar um talhão.
 *  - O número do talhão deve ser único dentro da mesma fazenda.
 *  - Apenas campos não-nulos são aplicados na atualização.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TalhaoService {

    private final TalhaoRepository talhaoRepository;
    private final FazendaRepository fazendaRepository;
    private final TalhaoMapper mapper;

    // ─── CREATE ──────────────────────────────────────────────────────────────

    public TalhaoResponseDTO criar(TalhaoCreateRequestDTO dto) {
        Fazenda fazenda = fazendaRepository.findById(dto.fazendaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Fazenda não encontrada com o ID: " + dto.fazendaId()));

        if (talhaoRepository.existsByNumeroAndFazendaId(dto.numero(), dto.fazendaId())) {
            throw new RegraDeNegocioException(
                    "Já existe um talhão com o número '" + dto.numero() + "' nesta fazenda.");
        }

        Talhao talhao = new Talhao();
        talhao.setNumero(dto.numero());
        talhao.setArea(dto.area());
        talhao.setCultura(dto.cultura());
        talhao.setDataPlantio(dto.dataPlantio());
        talhao.setLimiteCriticoUmidade(dto.limiteCriticoUmidade());
        talhao.setFazenda(fazenda);

        return mapper.toResponseDTO(talhaoRepository.save(talhao));
    }

    // ─── READ ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public TalhaoResponseDTO buscarPorId(Long id) {
        return talhaoRepository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Talhão não encontrado com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<TalhaoResponseDTO> listarPorFazenda(Long fazendaId) {
        if (!fazendaRepository.existsById(fazendaId)) {
            throw new RecursoNaoEncontradoException("Fazenda não encontrada com o ID: " + fazendaId);
        }
        return talhaoRepository.findByFazendaId(fazendaId).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<TalhaoResponseDTO> listarPorFazendaPaginado(Long fazendaId, Pageable pageable) {
        if (!fazendaRepository.existsById(fazendaId)) {
            throw new RecursoNaoEncontradoException("Fazenda não encontrada com o ID: " + fazendaId);
        }
        return talhaoRepository.findByFazendaId(fazendaId, pageable).map(mapper::toResponseDTO);
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    public TalhaoResponseDTO atualizar(Long id, TalhaoUpdateRequestDTO dto) {
        Talhao talhao = talhaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Talhão não encontrado com o ID: " + id));

        if (dto.numero() != null && !dto.numero().equals(talhao.getNumero())) {
            if (talhaoRepository.existsByNumeroAndFazendaId(dto.numero(), talhao.getFazenda().getId())) {
                throw new RegraDeNegocioException(
                        "Já existe um talhão com o número '" + dto.numero() + "' nesta fazenda.");
            }
            talhao.setNumero(dto.numero());
        }
        if (dto.area()                 != null) talhao.setArea(dto.area());
        if (dto.cultura()              != null) talhao.setCultura(dto.cultura());
        if (dto.dataPlantio()          != null) talhao.setDataPlantio(dto.dataPlantio());
        if (dto.limiteCriticoUmidade() != null) talhao.setLimiteCriticoUmidade(dto.limiteCriticoUmidade());

        return mapper.toResponseDTO(talhaoRepository.save(talhao));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    public void excluir(Long id) {
        if (!talhaoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Talhão não encontrado com o ID: " + id);
        }
        talhaoRepository.deleteById(id);
    }
}
