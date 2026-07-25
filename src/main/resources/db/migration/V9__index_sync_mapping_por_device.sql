-- V9: Índice para SyncService.getSyncStatus, que antes fazia findAll() e filtrava
-- em memória para achar o mapeamento mais recente de um dispositivo — custo que
-- cresce com o histórico de sync de TODOS os dispositivos, não só do consultado.

CREATE INDEX idx_sync_mapping_device_criado ON sync_id_mapping(device_id, criado_em DESC);
