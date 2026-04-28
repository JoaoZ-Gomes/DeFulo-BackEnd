package com.defulo.api.features.usuario.model;

import java.time.LocalDateTime;

/**
 * Interface que define o contrato de acessores para a entidade Usuario.
 * Garante que subclasses mantenham compatibilidade de API.
 */
public interface IUsuario {

    Long getId();
    void setId(Long id);

    String getNome();
    void setNome(String nome);

    String getEmail();
    void setEmail(String email);

    String getSenha();
    void setSenha(String senha);

    String getCpf();
    void setCpf(String cpf);

    String getTelefone();
    void setTelefone(String telefone);

    Perfil getPerfil();
    void setPerfil(Perfil perfil);

    Long getTalhaoId();
    void setTalhaoId(Long talhaoId);

    LocalDateTime getDataCriacao();
    void setDataCriacao(LocalDateTime dataCriacao);

    LocalDateTime getDataAtualizacao();
    void setDataAtualizacao(LocalDateTime dataAtualizacao);
}