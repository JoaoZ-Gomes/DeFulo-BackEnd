-- V5: Criação da tabela de mapeamento de IDs locais → remotos (sync idempotência)
-- Garante que operações offline já processadas não sejam reprocessadas
-- mesmo que o cliente reenvie o mesmo item em múltiplas tentativas de sync.

CREATE TABLE sync_id_mapping (
    id              SERIAL PRIMARY KEY,
    local_id        VARCHAR(255)  NOT NULL,   -- UUID gerado pelo dispositivo
    remote_id       BIGINT        NOT NULL,   -- ID gerado pelo servidor
    entity_type     VARCHAR(50)   NOT NULL,   -- ex: "FAZENDA", "TALHAO", "EVENTO"
    device_id       VARCHAR(255)  NOT NULL,   -- identificador do dispositivo
    sync_id         VARCHAR(255),             -- ID do lote de sync que criou este registro
    criado_em       TIMESTAMP     NOT NULL DEFAULT NOW(),

    -- Garante unicidade por (localId, deviceId, entityType)
    CONSTRAINT uq_sync_mapping UNIQUE (local_id, device_id, entity_type)
);

CREATE INDEX idx_sync_mapping_local     ON sync_id_mapping(local_id, device_id);
CREATE INDEX idx_sync_mapping_entity    ON sync_id_mapping(entity_type, remote_id);
CREATE INDEX idx_sync_mapping_device    ON sync_id_mapping(device_id);
