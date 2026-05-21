package com.defulo.api.features.usuario.model;

public interface IUsuario {

    Long getId();

    void setId(Long id);

    String getNome();

    void setNome(String nome);

    String getEmail();

    void setEmail(String email);

    String getSenha();

    void setSenha(String senha);

    Perfil getPerfil();

    void setPerfil(Perfil perfil);

    Long getTalhaoId();

    void setTalhaoId(Long talhaoId);
}