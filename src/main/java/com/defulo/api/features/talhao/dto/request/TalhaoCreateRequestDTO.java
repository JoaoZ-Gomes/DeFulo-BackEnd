package com.defulo.api.features.talhao.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO de entrada para criação de Talhão.
 * fazendaId vincula o talhão à fazenda correspondente.
 */
public record TalhaoCreateRequestDTO(

        @NotBlank(message = "O número do talhão é obrigatório.")
        @Size(max = 10, message = "O número do talhão deve ter no máximo 10 caracteres.")
        String numero,

        @NotNull(message = "A área é obrigatória.")
        @Positive(message = "A área deve ser um valor positivo.")
        Double area,

        @NotBlank(message = "A cultura é obrigatória.")
        @Size(max = 50, message = "A cultura deve ter no máximo 50 caracteres.")
        String cultura,

        LocalDate dataPlantio,

        @Positive(message = "O limite crítico de umidade deve ser positivo.")
        Double limiteCriticoUmidade,

        @NotNull(message = "O ID da fazenda é obrigatório.")
        Long fazendaId

) {}
