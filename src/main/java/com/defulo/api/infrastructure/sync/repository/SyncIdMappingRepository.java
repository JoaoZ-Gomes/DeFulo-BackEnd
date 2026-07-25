package com.defulo.api.infrastructure.sync.repository;

import com.defulo.api.infrastructure.sync.model.SyncIdMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SyncIdMappingRepository extends JpaRepository<SyncIdMapping, Long> {

    /** Verifica se um item já foi processado (idempotência). */
    boolean existsByLocalIdAndDeviceIdAndEntityType(String localId, String deviceId, String entityType);

    /** Busca o mapeamento para obter o remoteId de um item já processado. */
    Optional<SyncIdMapping> findByLocalIdAndDeviceIdAndEntityType(
            String localId, String deviceId, String entityType);

    /** Mapeamento mais recente de um dispositivo — evita carregar a tabela inteira em memória. */
    Optional<SyncIdMapping> findFirstByDeviceIdOrderByCriadoEmDesc(String deviceId);
}
