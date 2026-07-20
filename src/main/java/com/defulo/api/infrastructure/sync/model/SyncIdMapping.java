package com.defulo.api.infrastructure.sync.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade que registra o mapeamento entre IDs locais (dispositivo offline)
 * e IDs remotos (servidor), garantindo idempotência no push sync.
 */
@Entity
@Table(
    name = "sync_id_mapping",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_sync_mapping",
        columnNames = {"local_id", "device_id", "entity_type"}
    )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncIdMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** UUID gerado pelo dispositivo para identificar o recurso offline. */
    @Column(name = "local_id", nullable = false, length = 255)
    private String localId;

    /** ID gerado pelo servidor ao persistir o recurso. */
    @Column(name = "remote_id", nullable = false)
    private Long remoteId;

    /** Tipo da entidade (ex: "FAZENDA", "TALHAO", "EVENTO"). */
    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    /** Identificador do dispositivo que enviou a operação. */
    @Column(name = "device_id", nullable = false, length = 255)
    private String deviceId;

    /** ID do lote de sync que processou este item. */
    @Column(name = "sync_id", length = 255)
    private String syncId;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
    }
}
