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
import com.defulo.api.infrastructure.security.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TalhaoService {

    private final TalhaoRepository talhaoRepository;
    private final FazendaRepository fazendaRepository;
    private final TalhaoMapper mapper;
    private final AuthorizationService authorizationService;

    public TalhaoResponseDTO criar(TalhaoCreateRequestDTO dto) {
        authorizationService.exigirPodeGerenciarTalhao();

        Fazenda fazenda = buscarFazenda(dto.fazendaId());
        authorizationService.exigirAcessoAFazenda(fazenda);

        if (talhaoRepository.existsByNumeroAndFazendaId(dto.numero(), dto.fazendaId())) {
            throw new RegraDeNegocioException(
                    "Ja existe um talhao com o numero '" + dto.numero() + "' nesta fazenda.");
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

    @Transactional(readOnly = true)
    public TalhaoResponseDTO buscarPorId(Long id) {
        Talhao talhao = buscarEntidade(id);
        authorizationService.exigirAcessoATalhao(talhao);
        return mapper.toResponseDTO(talhao);
    }

    @Transactional(readOnly = true)
    public List<TalhaoResponseDTO> listarPorFazenda(Long fazendaId) {
        Fazenda fazenda = buscarFazenda(fazendaId);
        authorizationService.exigirAcessoAFazenda(fazenda);

        return talhaoRepository.findByFazendaId(fazendaId).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<TalhaoResponseDTO> listarPorFazendaPaginado(Long fazendaId, Pageable pageable) {
        Fazenda fazenda = buscarFazenda(fazendaId);
        authorizationService.exigirAcessoAFazenda(fazenda);

        return talhaoRepository.findByFazendaId(fazendaId, pageable).map(mapper::toResponseDTO);
    }

    public TalhaoResponseDTO atualizar(Long id, TalhaoUpdateRequestDTO dto) {
        authorizationService.exigirPodeGerenciarTalhao();

        Talhao talhao = buscarEntidade(id);
        authorizationService.exigirAcessoATalhao(talhao);

        if (dto.numero() != null && !dto.numero().equals(talhao.getNumero())) {
            if (talhaoRepository.existsByNumeroAndFazendaId(dto.numero(), talhao.getFazenda().getId())) {
                throw new RegraDeNegocioException(
                        "Ja existe um talhao com o numero '" + dto.numero() + "' nesta fazenda.");
            }
            talhao.setNumero(dto.numero());
        }
        if (dto.area() != null) {
            talhao.setArea(dto.area());
        }
        if (dto.cultura() != null) {
            talhao.setCultura(dto.cultura());
        }
        if (dto.dataPlantio() != null) {
            talhao.setDataPlantio(dto.dataPlantio());
        }
        if (dto.limiteCriticoUmidade() != null) {
            talhao.setLimiteCriticoUmidade(dto.limiteCriticoUmidade());
        }

        return mapper.toResponseDTO(talhaoRepository.save(talhao));
    }

    public void excluir(Long id) {
        authorizationService.exigirPodeGerenciarTalhao();

        Talhao talhao = buscarEntidade(id);
        authorizationService.exigirAcessoATalhao(talhao);
        talhaoRepository.delete(talhao);
    }

    private Fazenda buscarFazenda(Long id) {
        return fazendaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fazenda nao encontrada com o ID: " + id));
    }

    private Talhao buscarEntidade(Long id) {
        return talhaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Talhao nao encontrado com o ID: " + id));
    }
}
