package com.defulo.api.features.usuario.dto.request;

import jakarta.validation.constraints.Size;

/**
 * DTO de atualização parcial de usuário.
 * Campos nulos são ignorados durante o update.
 */
public record UsuarioUpdateRequestDTO(

        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String nome,

        @Size(max = 20, message = "O telefone pode ter no máximo 20 caracteres.")
        String telefone,

        Long talhaoId
) { }
