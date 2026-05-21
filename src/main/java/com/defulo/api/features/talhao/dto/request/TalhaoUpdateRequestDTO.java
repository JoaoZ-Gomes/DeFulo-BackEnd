package com.defulo.api.features.talhao.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO de atualização parcial de Talhão.
 * Campos nulos são ignorados durante a atualização.
 */
public record TalhaoUpdateRequestDTO(

        @Size(max = 10, message = "O número deve ter no máximo 10 caracteres.")
        String numero,

        @Positive(message = "A área deve ser um valor positivo.")
        Double area,

        @Size(max = 50, message = "A cultura deve ter no máximo 50 caracteres.")
        String cultura,

        LocalDate dataPlantio,

        @Positive(message = "O limite crítico de umidade deve ser positivo.")
        Double limiteCriticoUmidade

) {}
