package com.defulo.api.infrastructure.sync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Resposta completa do pull
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyncPullResponseDTO {
    
    @JsonProperty("items")
    private List<SyncPullItemDTO> items;
    
    @JsonProperty("serverTimestamp")
    private String serverTimestamp;  // ISO 8601 - usar como próximo "since"
}
