package com.defulo.api.features.evento.dto.response;

import com.defulo.api.features.evento.model.TipoEvento;
import java.time.LocalDateTime;

/**
 * DTO de resposta para Evento de Manejo.
 * Nunca expõe entidades diretamente para evitar lazy-loading e loops de serialização.
 */
public record EventoResponseDTO(

        Long id,
        String nome,
        String descricao,
        LocalDateTime data,
        TipoEvento tipo,
        String quantidade,

        Long talhaoId,
        String talhaoNumero,

        Long rtvId,
        String rtvNome

) {}
