package com.defulo.api.features.auth.dto;

import com.defulo.api.features.usuario.model.Perfil;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de autenticação com token JWT")
public record LoginResponse(
    @Schema(description = "Token JWT válido por 2 horas", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String token,
    
    @Schema(description = "ID do usuário", example = "1")
    Long id,
    
    @Schema(description = "Nome do usuário", example = "Administrador DeFulo")
    String nome,
    
    @Schema(description = "Email do usuário", example = "admin@defulo.com")
    String email,
    
    @Schema(description = "Perfil/papel do usuário", example = "ADM")
    Perfil perfil
) {}
