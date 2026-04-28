package com.defulo.api.features.usuario.dto;

import com.defulo.api.features.usuario.model.Perfil;
import lombok.Data;

@Data
public class UsuarioDTO {

    private Long id;

    private String nome;

    private String email;

    private Perfil perfil;

    private Long talhaoId;

}