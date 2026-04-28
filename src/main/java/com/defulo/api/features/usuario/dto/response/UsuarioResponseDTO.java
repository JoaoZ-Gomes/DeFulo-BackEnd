package com.defulo.api.features.usuario.dto.response;

import java.time.LocalDateTime;

import com.defulo.api.features.usuario.model.Perfil;

/**
 * DTO de resposta do usuário. Não expõe a senha por segurança.
 */
public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        String cpf,
        String telefone,
        Perfil perfil,
        Long talhaoId,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) { }
