package com.defulo.api.features.inspecao.model;

/**
 * Nível de infestação/severidade detectado na inspeção fitossanitária.
 *
 * <p>Baseado na Escala Diagramática padrão para tomada de decisão agronômica.
 * Os valores ALTO e CRITICO indicam que o Nível de Ação foi atingido e
 * requerem intervenção imediata de controle fitossanitário.</p>
 *
 * <p>Espelho exato do enum {@code NivelInfestacao} no Flutter.</p>
 */
public enum NivelInfestacao {

    /** Praga ausente — apenas monitoramento de rotina. */
    AUSENTE("Ausente"),

    /** Baixa presença — monitorar com frequência aumentada. */
    BAIXO("Baixo"),

    /** Presença moderada — atenção redobrada. Próximo do limiar. */
    MEDIO("Médio"),

    /** Nível de ação atingido — acionar controle imediatamente. */
    ALTO("Alto"),

    /** Infestação generalizada — dano econômico em curso. */
    CRITICO("Crítico");

    private final String descricao;

    NivelInfestacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Retorna true se o nível exige ação imediata de controle.
     * Critério: ALTO ou CRITICO = nível de ação atingido.
     */
    public boolean exigeAcaoImediata() {
        return this == ALTO || this == CRITICO;
    }
}
