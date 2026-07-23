package com.defulo.api.features.inspecao.model;

/**
 * Estágio fenológico da cultura no momento da inspeção.
 *
 * <p>O estágio fenológico é determinante para a tomada de decisão de manejo:
 * o limiar de dano econômico e a janela de aplicação de defensivos variam
 * significativamente entre os estágios.</p>
 *
 * <p>Espelho exato do enum {@code EstagioFenologico} no Flutter.</p>
 */
public enum EstagioFenologico {

    GERMINACAO("Germinação"),
    VEGETATIVO("Vegetativo"),
    FLORESCIMENTO("Florescimento"),
    ENCHIMENTO_GRAOS("Enchimento de Grãos"),
    MATURACAO("Maturação"),
    COLHEITA("Colheita");

    private final String descricao;

    EstagioFenologico(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
