package com.defulo.api.infrastructure.sync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Status de sincronização de um dispositivo
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyncStatusDTO {
    
    @JsonProperty("deviceId")
    private String deviceId;
    
    @JsonProperty("lastSyncId")
    private String lastSyncId;
    
    @JsonProperty("lastSyncStatus")
    private String lastSyncStatus;  // 'RUNNING' | 'DONE' | 'PARTIAL' | 'FAILED'
    
    @JsonProperty("lastSyncTimestamp")
    private String lastSyncTimestamp;
    
    @JsonProperty("pendingItemsCount")
    private Integer pendingItemsCount;
}
