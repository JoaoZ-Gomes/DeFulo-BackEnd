package com.defulo.api.infrastructure.sync.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Resposta completa do servidor para push
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyncPushResponseDTO {
    
    @JsonProperty("syncId")
    private String syncId;  // UUID da sessão
    
    @JsonProperty("items")
    private List<SyncItemResponseDTO> items;
    
    @JsonProperty("totalProcessed")
    private Integer totalProcessed;
    
    @JsonProperty("successCount")
    private Integer successCount;
    
    @JsonProperty("conflictCount")
    private Integer conflictCount;
    
    @JsonProperty("errorCount")
    private Integer errorCount;
}
