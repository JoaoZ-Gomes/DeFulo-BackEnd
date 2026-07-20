package com.defulo.api.features.talhao.repository;

import com.defulo.api.features.talhao.model.Talhao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TalhaoRepository extends JpaRepository<Talhao, Long> {

    /** Lista todos os talhões de uma fazenda. */
    List<Talhao> findByFazendaId(Long fazendaId);

    /** Lista paginada de talhões por fazenda. */
    Page<Talhao> findByFazendaId(Long fazendaId, Pageable pageable);

    /** Verifica unicidade de número dentro da mesma fazenda. */
    boolean existsByNumeroAndFazendaId(String numero, Long fazendaId);

    /** Lista talhões modificados após um determinado momento (para pull sync incremental). */
    List<Talhao> findByDataAtualizacaoAfter(LocalDateTime since);
}
