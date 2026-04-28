package com.defulo.api.features.engenheiro.dto.response;

import java.time.LocalDateTime;

public record EngenheiroResponseDTO(
        Long id,
        String nome,
        String email,
        String cpf,
        String telefone,
        String especialidade,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) { }
