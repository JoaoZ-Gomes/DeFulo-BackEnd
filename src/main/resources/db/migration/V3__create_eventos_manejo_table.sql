-- V3: Criação da tabela de Eventos de Manejo
-- Depende de: V2 (talhoes), V1 (usuarios/rtv)

CREATE TABLE eventos_manejo (
    id          SERIAL PRIMARY KEY,
    nome        VARCHAR(100)  NOT NULL,
    descricao   TEXT          NOT NULL,
    data        TIMESTAMP     NOT NULL DEFAULT NOW(),
    tipo        VARCHAR(30)   NOT NULL,
    quantidade  VARCHAR(100),
    talhao_id   BIGINT,
    rtv_id      BIGINT,

    CONSTRAINT fk_evento_talhao FOREIGN KEY (talhao_id)
        REFERENCES talhoes(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_evento_rtv    FOREIGN KEY (rtv_id)
        REFERENCES usuarios(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_evento_tipo CHECK (
        tipo IN ('IRRIGACAO', 'ADUBACAO', 'APLICACAO', 'PLANTIO', 'COLHEITA')
    )
);

CREATE INDEX idx_evento_talhao ON eventos_manejo(talhao_id);
CREATE INDEX idx_evento_data   ON eventos_manejo(data DESC);
CREATE INDEX idx_evento_tipo   ON eventos_manejo(tipo);
