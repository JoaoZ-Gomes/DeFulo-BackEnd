package com.defulo.api.features.evento.repository;

import com.defulo.api.features.evento.model.EventoManejo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<EventoManejo, Long> {

    /** Lista todos os eventos de um talhão, para uso simples. */
    List<EventoManejo> findByTalhaoId(Long talhaoId);

    /** Lista paginada de eventos de um talhão, ordenada por data desc. */
    Page<EventoManejo> findByTalhaoId(Long talhaoId, Pageable pageable);

    /** Lista eventos modificados após um determinado momento (para pull sync incremental). */
    List<EventoManejo> findByDataAtualizacaoAfter(LocalDateTime since);

    /** Conta quantos eventos de manejo um talhão possui — usado para bloquear exclusão com dependentes. */
    long countByTalhaoId(Long talhaoId);
}
