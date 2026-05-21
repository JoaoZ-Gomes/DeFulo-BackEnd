package com.defulo.api.features.evento.dto.request;

import jakarta.validation.constraints.Size;

public record EventoUpdateDTO (

        @Size(min = 3, max = 100,message = "O nome deve ter entre 3 e 100 caracteres.")
        String nome,


        @Size(min = 3 ,max = 5000,message = "O nome deve ter entre 3 e 5000 caracteres.")
        String descricao



){
}
