package com.defulo.api.features.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para autenticação (login)")
public record LoginRequest(
    @NotBlank @Email 
    @Schema(description = "Email do usuário", example = "admin@defulo.com")
    String email,
    
    @NotBlank 
    @Schema(description = "Senha do usuário", example = "admin123")
    String senha
) {}
