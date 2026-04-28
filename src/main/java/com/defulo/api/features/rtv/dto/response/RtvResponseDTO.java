package com.defulo.api.features.rtv.dto.response;

import java.time.LocalDateTime;

/**
 * DTO de resposta para RTV.
 */
public record RtvResponseDTO(
        Long id,
        String nome,
        String email,
        String cpf,
        String telefone,
        String regiao,
        String codigoRtv,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) { }
