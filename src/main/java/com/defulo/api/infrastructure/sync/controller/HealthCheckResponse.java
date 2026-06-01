package com.defulo.api.infrastructure.sync.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Resposta de health check
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthCheckResponse {
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("version")
    private String version;
}

/**
 * Requisição de refresh de token
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class TokenRefreshRequest {
    @JsonProperty("token")
    private String token;
}

/**
 * Resposta de refresh de token
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class TokenRefreshResponse {
    @JsonProperty("token")
    private String token;
}
