package com.defulo.api.features.fazenda.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de resposta para Fazenda.
 * Inclui dados do produtor para exibição sem expor a entidade.
 */
public record FazendaResponseDTO(
        Long id,
        String nome,
        BigDecimal areaTotal,
        String cultura,
        Long produtorId,
        String nomeProdutor,
        LocalDateTime dataCriacao
) {}
