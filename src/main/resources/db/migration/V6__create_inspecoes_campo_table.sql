-- V6: Criação da tabela de inspeções de campo fitossanitárias
-- Módulo: Caderno de Monitoramento e Laudos Técnicos Local-First

CREATE TABLE inspecoes_campo (
    id                  BIGSERIAL PRIMARY KEY,
    local_id            VARCHAR(36)    NOT NULL UNIQUE, -- UUID v4 gerado no mobile para idempotência
    device_id           VARCHAR(255)   NOT NULL,        -- identificador do dispositivo emissor
    data_inspecao       TIMESTAMP      NOT NULL,        -- data/hora real de quando o agrônomo fez o laudo offline
    estagio_fenologico  VARCHAR(30)    NOT NULL,        -- ex: VEGETATIVO, FLORESCIMENTO, MATURACAO
    nivel_infestacao    VARCHAR(20)    NOT NULL,        -- ex: AUSENTE, BAIXO, MEDIO, ALTO, CRITICO
    pragas              TEXT,                           -- JSON array das pragas detectadas
    observacoes_tecnicas TEXT,                         -- relato de campo do agrônomo
    recomendacao_manejo  TEXT,                         -- prescrição de manejo/tratamento
    latitude            VARCHAR(20),                    -- coordenada GPS coletada via satélite
    longitude           VARCHAR(20),                    -- coordenada GPS
    foto_url            VARCHAR(500),                   -- URL pública da evidência fotográfica
    
    talhao_id           BIGINT         NOT NULL,
    fazenda_id          BIGINT         NOT NULL,
    engenheiro_id       BIGINT         NOT NULL,
    
    criado_em           TIMESTAMP      NOT NULL DEFAULT NOW(),
    atualizado_em       TIMESTAMP      NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_inspecao_talhao FOREIGN KEY (talhao_id)
        REFERENCES talhoes(id) ON DELETE CASCADE,
        
    CONSTRAINT fk_inspecao_fazenda FOREIGN KEY (fazenda_id)
        REFERENCES fazendas(id) ON DELETE CASCADE,
        
    CONSTRAINT fk_inspecao_engenheiro FOREIGN KEY (engenheiro_id)
        REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE INDEX idx_inspecao_talhao     ON inspecoes_campo(talhao_id);
CREATE INDEX idx_inspecao_fazenda    ON inspecoes_campo(fazenda_id);
CREATE INDEX idx_inspecao_engenheiro ON inspecoes_campo(engenheiro_id);
CREATE INDEX idx_inspecao_data       ON inspecoes_campo(data_inspecao);
CREATE INDEX idx_inspecao_nivel      ON inspecoes_campo(nivel_infestacao);
