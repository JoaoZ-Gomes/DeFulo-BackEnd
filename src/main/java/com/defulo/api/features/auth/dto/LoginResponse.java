package com.defulo.api.features.auth.dto;

import com.defulo.api.features.usuario.model.Perfil;

public record LoginResponse(
    String token,
    Long id,
    String nome,
    String email,
    Perfil perfil
) {}
