package com.defulo.api.features.talhao.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta para Talhão.
 * Inclui dados da fazenda associada para contexto sem expor a entidade.
 */
public record TalhaoResponseDTO(
        Long id,
        String numero,
        Double area,
        String cultura,
        LocalDate dataPlantio,
        Double limiteCriticoUmidade,
        Long fazendaId,
        String nomeFazenda,
        LocalDateTime dataCriacao
) {}
