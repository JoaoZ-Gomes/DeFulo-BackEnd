package com.defulo.api.features.fazenda.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para criação de Fazenda.
 * produtorId vincula a fazenda ao produtor dono.
 */
public record FazendaCreateRequestDTO(

        @NotBlank(message = "O nome da fazenda é obrigatório.")
        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
        String nome,

        @NotNull(message = "A área total é obrigatória.")
        @Positive(message = "A área total deve ser um valor positivo.")
        Double areaTotal,

        @NotBlank(message = "A cultura é obrigatória.")
        @Size(max = 50, message = "A cultura deve ter no máximo 50 caracteres.")
        String cultura,

        @NotNull(message = "O ID do produtor é obrigatório.")
        Long produtorId

) {}
