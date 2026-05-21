package com.defulo.api.features.fazenda.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO de atualização de Fazenda.
 * Todos os campos são opcionais — apenas os não-nulos serão aplicados.
 */
public record FazendaUpdateRequestDTO(

        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
        String nome,

        @Positive(message = "A área total deve ser um valor positivo.")
        Double areaTotal,

        @Size(max = 50, message = "A cultura deve ter no máximo 50 caracteres.")
        String cultura

) {}
