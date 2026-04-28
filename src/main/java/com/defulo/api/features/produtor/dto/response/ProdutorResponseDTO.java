package com.defulo.api.features.produtor.dto.response;

import java.time.LocalDateTime;

public record ProdutorResponseDTO(
        Long id,
        String nome,
        String email,
        String cpf,
        String telefone,
        String propriedade,
        Double areaTotal,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) { }
