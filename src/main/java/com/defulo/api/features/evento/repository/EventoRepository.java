package com.defulo.api.features.evento.repository;

import com.defulo.api.features.evento.model.EventoManejo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<EventoManejo, Long> {

    /** Lista todos os eventos de um talhão, para uso simples. */
    List<EventoManejo> findByTalhaoId(Long talhaoId);

    /** Lista paginada de eventos de um talhão, ordenada por data desc. */
    Page<EventoManejo> findByTalhaoId(Long talhaoId, Pageable pageable);
}
