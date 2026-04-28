package com.defulo.api.features.rtv.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para criação de um RTV.
 */
public record RtvCreateRequestDTO(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String nome,

        @NotBlank(message = "O email é obrigatório.")
        @Email(message = "O formato do email é inválido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
        String senha,

        @Size(min = 11, max = 14, message = "O CPF deve ter entre 11 e 14 caracteres.")
        String cpf,

        @Size(max = 20, message = "O telefone pode ter no máximo 20 caracteres.")
        String telefone,

        String regiao,
        
        String codigoRtv
) { }
