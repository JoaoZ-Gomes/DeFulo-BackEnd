package com.defulo.api.features.fazenda.service;

import com.defulo.api.features.fazenda.dto.request.FazendaCreateRequestDTO;
import com.defulo.api.features.fazenda.dto.request.FazendaUpdateRequestDTO;
import com.defulo.api.features.fazenda.dto.response.FazendaResponseDTO;
import com.defulo.api.features.fazenda.mapper.FazendaMapper;
import com.defulo.api.features.fazenda.model.Fazenda;
import com.defulo.api.features.fazenda.repository.FazendaRepository;
import com.defulo.api.features.produtor.model.Produtor;
import com.defulo.api.features.produtor.repository.ProdutorRepository;
import com.defulo.api.features.talhao.repository.TalhaoRepository;
import com.defulo.api.features.usuario.model.Perfil;
import com.defulo.api.features.usuario.model.Usuario;
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
public class FazendaService {

    private final FazendaRepository fazendaRepository;
    private final ProdutorRepository produtorRepository;
    private final TalhaoRepository talhaoRepository;
    private final FazendaMapper mapper;
    private final AuthorizationService authorizationService;

    public FazendaResponseDTO criar(FazendaCreateRequestDTO dto) {
        authorizationService.exigirPodeGerenciarFazenda();
        authorizationService.exigirAcessoAoProdutor(dto.produtorId());

        Produtor produtor = produtorRepository.findById(dto.produtorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Produtor nao encontrado com o ID: " + dto.produtorId()));

        if (fazendaRepository.existsByNomeAndProdutorId(dto.nome(), dto.produtorId())) {
            throw new RegraDeNegocioException(
                    "Este produtor ja possui uma fazenda com o nome: '" + dto.nome() + "'.");
        }

        Fazenda fazenda = new Fazenda();
        fazenda.setNome(dto.nome());
        fazenda.setAreaTotal(dto.areaTotal());
        fazenda.setCultura(dto.cultura());
        fazenda.setProdutor(produtor);

        return mapper.toResponseDTO(fazendaRepository.save(fazenda));
    }

    @Transactional(readOnly = true)
    public Page<FazendaResponseDTO> listar(Pageable pageable) {
        Usuario usuario = authorizationService.getUsuarioAutenticado();
        if (authorizationService.isAcessoAmplo(usuario)) {
            return fazendaRepository.findAll(pageable).map(mapper::toResponseDTO);
        }
        if (usuario.getPerfil() == Perfil.PRODUTOR) {
            return fazendaRepository.findByProdutorId(usuario.getId(), pageable).map(mapper::toResponseDTO);
        }
        authorizationService.exigirPodeGerenciarFazenda();
        return Page.empty(pageable);
    }

    @Transactional(readOnly = true)
    public List<FazendaResponseDTO> listarPorProdutor(Long produtorId) {
        authorizationService.exigirAcessoAoProdutor(produtorId);

        if (!produtorRepository.existsById(produtorId)) {
            throw new RecursoNaoEncontradoException("Produtor nao encontrado com o ID: " + produtorId);
        }
        return fazendaRepository.findByProdutorId(produtorId).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public FazendaResponseDTO buscarPorId(Long id) {
        Fazenda fazenda = buscarEntidade(id);
        authorizationService.exigirAcessoAFazenda(fazenda);
        return mapper.toResponseDTO(fazenda);
    }

    public FazendaResponseDTO atualizar(Long id, FazendaUpdateRequestDTO dto) {
        authorizationService.exigirPodeGerenciarFazenda();

        Fazenda fazenda = buscarEntidade(id);
        authorizationService.exigirAcessoAFazenda(fazenda);

        if (dto.nome() != null) {
            boolean nomeConflito = fazendaRepository
                    .existsByNomeAndProdutorId(dto.nome(), fazenda.getProdutor().getId());
            if (nomeConflito && !dto.nome().equals(fazenda.getNome())) {
                throw new RegraDeNegocioException(
                        "Este produtor ja possui outra fazenda com o nome: '" + dto.nome() + "'.");
            }
            fazenda.setNome(dto.nome());
        }
        if (dto.areaTotal() != null) {
            fazenda.setAreaTotal(dto.areaTotal());
        }
        if (dto.cultura() != null) {
            fazenda.setCultura(dto.cultura());
        }

        return mapper.toResponseDTO(fazendaRepository.save(fazenda));
    }

    public void excluir(Long id) {
        authorizationService.exigirPodeGerenciarFazenda();

        Fazenda fazenda = buscarEntidade(id);
        authorizationService.exigirAcessoAFazenda(fazenda);

        long totalTalhoes = talhaoRepository.countByFazendaId(id);
        if (totalTalhoes > 0) {
            throw new RegraDeNegocioException(
                    "Não é possível excluir esta fazenda pois ela possui " + totalTalhoes
                            + " talhão(ões) cadastrado(s). Exclua os talhões primeiro.");
        }

        fazendaRepository.delete(fazenda);
    }

    private Fazenda buscarEntidade(Long id) {
        return fazendaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fazenda nao encontrada com o ID: " + id));
    }
}
