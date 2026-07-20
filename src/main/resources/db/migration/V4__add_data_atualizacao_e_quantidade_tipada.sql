-- V4: Adiciona coluna data_atualizacao e refatora campo quantidade em eventos_manejo
-- Necessário para:
--   1. Pull sync incremental (filtrar por data_atualizacao > :since)
--   2. Tipagem correta da quantidade (valor numérico + unidade separados)

-- ── Fazendas ──────────────────────────────────────────────────────────────────
ALTER TABLE fazendas
    ADD COLUMN IF NOT EXISTS data_atualizacao TIMESTAMP;

UPDATE fazendas SET data_atualizacao = data_criacao WHERE data_atualizacao IS NULL;

-- ── Talhoes ───────────────────────────────────────────────────────────────────
ALTER TABLE talhoes
    ADD COLUMN IF NOT EXISTS data_atualizacao TIMESTAMP;

UPDATE talhoes SET data_atualizacao = data_criacao WHERE data_atualizacao IS NULL;

-- ── Eventos de Manejo ─────────────────────────────────────────────────────────
-- Adiciona novos campos tipados
ALTER TABLE eventos_manejo
    ADD COLUMN IF NOT EXISTS quantidade_valor  DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS quantidade_unidade VARCHAR(20),
    ADD COLUMN IF NOT EXISTS data_atualizacao   TIMESTAMP;

-- Migra dados legados de 'quantidade' (String) para os novos campos
-- Tenta interpretar como número puro; o restante fica em quantidade_unidade
UPDATE eventos_manejo
SET quantidade_valor   = NULL,
    quantidade_unidade = quantidade
WHERE quantidade IS NOT NULL
  AND quantidade_valor IS NULL;

UPDATE eventos_manejo SET data_atualizacao = data WHERE data_atualizacao IS NULL;

-- Remove a coluna legada após migração
ALTER TABLE eventos_manejo DROP COLUMN IF EXISTS quantidade;

-- Índices para queries de pull sync incremental
CREATE INDEX IF NOT EXISTS idx_fazenda_atualizacao  ON fazendas(data_atualizacao DESC);
CREATE INDEX IF NOT EXISTS idx_talhao_atualizacao   ON talhoes(data_atualizacao DESC);
CREATE INDEX IF NOT EXISTS idx_evento_atualizacao   ON eventos_manejo(data_atualizacao DESC);
