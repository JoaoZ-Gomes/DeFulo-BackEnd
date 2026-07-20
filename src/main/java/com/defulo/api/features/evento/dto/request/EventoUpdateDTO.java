package com.defulo.api.features.evento.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public record EventoUpdateDTO(

        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String nome,

        @Size(min = 3, max = 5000, message = "A descrição deve ter entre 3 e 5000 caracteres.")
        String descricao,

        @DecimalMin(value = "0.0", inclusive = false, message = "A quantidade deve ser maior que zero.")
        Double quantidadeValor,

        @Size(max = 20, message = "A unidade deve ter no máximo 20 caracteres.")
        String quantidadeUnidade

) {}
