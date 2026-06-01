package com.defulo.api.infrastructure.sync.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Item individual de sincronização enviado pelo cliente
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyncItemDTO {
    
    @JsonProperty("localId")
    private String localId;  // UUID local ou ID temporário
    
    @JsonProperty("entityType")
    private String entityType;  // 'usuario', 'fazenda', 'talhao', 'evento'
    
    @JsonProperty("operation")
    private String operation;  // 'CREATE', 'UPDATE', 'DELETE'
    
    @JsonProperty("payload")
    private Map<String, Object> payload;  // JSON da entidade
    
    @JsonProperty("localVersion")
    private Integer localVersion;
    
    @JsonProperty("checksum")
    private String checksum;  // SHA-256 do payload
    
    @JsonProperty("createdAt")
    private String createdAt;  // ISO 8601
}
