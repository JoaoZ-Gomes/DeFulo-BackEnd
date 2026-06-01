package com.defulo.api.infrastructure.sync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Item de resposta do pull
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyncPullItemDTO {
    
    @JsonProperty("remoteId")
    private Long remoteId;
    
    @JsonProperty("entityType")
    private String entityType;
    
    @JsonProperty("operation")
    private String operation;  // 'CREATE' | 'UPDATE' | 'DELETE'
    
    @JsonProperty("data")
    private Map<String, Object> data;
    
    @JsonProperty("updatedAt")
    private String updatedAt;
    
    @JsonProperty("version")
    private Integer version;
}
