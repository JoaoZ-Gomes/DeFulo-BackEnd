-- V2: Criação das tabelas de Fazenda e Talhão
-- Depende de: V1 (tabela usuarios)

CREATE TABLE fazendas (
    id          SERIAL PRIMARY KEY,
    nome        VARCHAR(100)       NOT NULL,
    area_total  DOUBLE PRECISION   NOT NULL,
    cultura     VARCHAR(50)        NOT NULL,
    produtor_id BIGINT             NOT NULL,
    data_criacao TIMESTAMP         NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_fazenda_produtor FOREIGN KEY (produtor_id)
        REFERENCES usuarios(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_fazenda_produtor ON fazendas(produtor_id);
CREATE INDEX idx_fazenda_nome     ON fazendas(nome);

-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE talhoes (
    id                      SERIAL PRIMARY KEY,
    numero                  VARCHAR(10)        NOT NULL,
    area                    DOUBLE PRECISION   NOT NULL,
    cultura                 VARCHAR(50)        NOT NULL,
    data_plantio            DATE,
    limite_critico_umidade  DOUBLE PRECISION,
    fazenda_id              BIGINT             NOT NULL,
    data_criacao            TIMESTAMP          NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_talhao_fazenda FOREIGN KEY (fazenda_id)
        REFERENCES fazendas(id)
        ON DELETE CASCADE,

    -- Número do talhão deve ser único dentro da mesma fazenda
    CONSTRAINT uq_talhao_numero_fazenda UNIQUE (numero, fazenda_id)
);

CREATE INDEX idx_talhao_fazenda ON talhoes(fazenda_id);
