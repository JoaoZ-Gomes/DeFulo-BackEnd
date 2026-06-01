package com.defulo.api.infrastructure.sync.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Requisição de push enviada pelo cliente com lote de operações
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyncPushRequestDTO {
    
    @JsonProperty("deviceId")
    private String deviceId;  // UUID do dispositivo
    
    @JsonProperty("items")
    private List<SyncItemDTO> items;  // Lote de operações
}
