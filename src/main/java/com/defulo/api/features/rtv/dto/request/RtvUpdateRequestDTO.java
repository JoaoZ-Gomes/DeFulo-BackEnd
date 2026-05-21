package com.defulo.api.features.rtv.dto.request;

import jakarta.validation.constraints.Size;

/**
 * DTO de atualização parcial do RTV.
 * Email, senha e CPF não são atualizáveis por este endpoint.
 */
public record RtvUpdateRequestDTO(

        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String nome,

        @Size(max = 20, message = "O telefone pode ter no máximo 20 caracteres.")
        String telefone,

        @Size(max = 100, message = "A região deve ter no máximo 100 caracteres.")
        String regiao,

        @Size(max = 50, message = "O código RTV deve ter no máximo 50 caracteres.")
        String codigoRtv

) {}
