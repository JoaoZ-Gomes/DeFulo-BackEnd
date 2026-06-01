package com.defulo.api.infrastructure.sync.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Resposta individual do servidor para cada item sincronizado
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyncItemResponseDTO {
    
    @JsonProperty("localId")
    private String localId;
    
    @JsonProperty("status")
    private String status;  // 'SUCCESS' | 'CONFLICT' | 'ERROR'
    
    @JsonProperty("remoteId")
    private Long remoteId;  // ID real do servidor (para CREATE)
    
    @JsonProperty("errorMessage")
    private String errorMessage;
    
    @JsonProperty("remoteData")
    private Map<String, Object> remoteData;  // Para resolver conflitos
    
    @JsonProperty("remoteVersion")
    private Integer remoteVersion;
}
