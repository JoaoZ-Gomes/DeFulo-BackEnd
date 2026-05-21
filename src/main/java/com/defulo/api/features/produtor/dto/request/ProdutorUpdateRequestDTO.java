package com.defulo.api.features.produtor.dto.request;

import jakarta.validation.constraints.Size;

/**
 * DTO de atualização parcial do Produtor.
 * Email, senha e CPF não são atualizáveis por este endpoint.
 */
public record ProdutorUpdateRequestDTO(

        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String nome,

        @Size(max = 20, message = "O telefone pode ter no máximo 20 caracteres.")
        String telefone,

        @Size(max = 150, message = "A propriedade deve ter no máximo 150 caracteres.")
        String propriedade,

        Double areaTotal

) {}
