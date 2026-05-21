package com.defulo.api.features.usuario.model;

/**
 * Enum que representa os perfis de acesso disponíveis no sistema DeFulo.
 * Cada perfil possui uma descrição legível para exibição.
 */
public enum Perfil {

    ADM("Administrador"),
    GESTOR("Gestor de Cooperativa"),
    PREFEITURA("Prefeitura"),
    ENGENHEIRO("Engenheiro Agrônomo"),
    RTV("Representante Técnico de Vendas"),
    PRODUTOR("Produtor Rural");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Retorna a descrição legível do perfil.
     *
     * @return descrição do perfil
     */
    public String getDescricao() {
        return descricao;
    }
}