package com.defulo.api.features.evento.dto.request;

import com.defulo.api.features.evento.model.TipoEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para criação de um Evento de Manejo.
 * talhaoId é obrigatório para vincular o evento a um talhão existente.
 */
public record EventoCreateRequestDto(

        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String nome,

        @NotBlank(message = "A descrição é obrigatória.")
        @Size(min = 3, max = 5000, message = "A descrição deve ter entre 3 e 5000 caracteres.")
        String descricao,

        @NotNull(message = "O tipo do evento é obrigatório.")
        TipoEvento tipo,

        @Size(max = 100, message = "A quantidade deve ter no máximo 100 caracteres.")
        String quantidade,

        @NotNull(message = "O ID do talhão é obrigatório.")
        Long talhaoId

) {}
