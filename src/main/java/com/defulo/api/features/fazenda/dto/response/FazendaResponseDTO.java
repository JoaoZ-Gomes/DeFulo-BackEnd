package com.defulo.api.features.fazenda.dto.response;

import java.time.LocalDateTime;

/**
 * DTO de resposta para Fazenda.
 * Inclui dados do produtor para exibição sem expor a entidade.
 */
public record FazendaResponseDTO(
        Long id,
        String nome,
        Double areaTotal,
        String cultura,
        Long produtorId,
        String nomeProdutor,
        LocalDateTime dataCriacao
) {}
