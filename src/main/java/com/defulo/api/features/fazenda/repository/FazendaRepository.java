package com.defulo.api.features.fazenda.repository;

import com.defulo.api.features.fazenda.model.Fazenda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FazendaRepository extends JpaRepository<Fazenda, Long> {

    /** Lista todas as fazendas de um produtor específico. */
    List<Fazenda> findByProdutorId(Long produtorId);

    /** Paginação das fazendas de um produtor. */
    Page<Fazenda> findByProdutorId(Long produtorId, Pageable pageable);

    /** Verifica se o produtor já tem uma fazenda com o mesmo nome. */
    boolean existsByNomeAndProdutorId(String nome, Long produtorId);
}
