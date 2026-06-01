package com.defulo.api.infrastructure.sync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Requisição de pull (fetch de dados modificados)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyncPullRequestDTO {
    
    @JsonProperty("since")
    private String since;  // Timestamp ISO 8601 - buscar registros modificados após este
    
    @JsonProperty("entityType")
    private String entityType;  // Opcional: filtrar por tipo de entidade
}
