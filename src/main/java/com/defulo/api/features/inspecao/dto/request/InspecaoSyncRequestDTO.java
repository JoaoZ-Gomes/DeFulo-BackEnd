package com.defulo.api.features.inspecao.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de entrada para sincronização de inspeções fitossanitárias.
 *
 * <p>Recebido via {@code POST /api/sync/inspecoes} em lote.
 * O campo {@code localId} (UUID v4 gerado pelo dispositivo) é usado
 * para garantir idempotência — o mesmo laudo pode ser reenviado múltiplas
 * vezes sem criar duplicatas no banco.</p>
 *
 * @param localId   UUID v4 gerado pelo app para idempotência
 * @param deviceId  Identificador do dispositivo de origem
 * @param fazendaId ID da fazenda (deve existir no servidor)
 * @param talhaoId  ID do talhão inspecionado (deve existir no servidor)
 * @param estagioFenologico Estágio da cultura (string do enum {@link com.defulo.api.features.inspecao.model.EstagioFenologico})
 * @param nivelInfestacao   Nível de infestação (string do enum {@link com.defulo.api.features.inspecao.model.NivelInfestacao})
 * @param pragas            Lista de pragas detectadas
 * @param observacoesTecnicas Observações livres do agrônomo
 * @param recomendacaoManejo  Prescrição técnica de manejo
 * @param latitude  Coordenada GPS (coletada via satélite, sem internet)
 * @param longitude Coordenada GPS
 * @param fotoBase64 Foto codificada em Base64 (opcional — null se não houver)
 * @param createdAt Timestamp de criação no dispositivo (ISO 8601 UTC)
 */
public record InspecaoSyncRequestDTO(

        @NotBlank(message = "localId é obrigatório para idempotência")
        String localId,

        @NotBlank(message = "deviceId é obrigatório para rastreabilidade")
        String deviceId,

        @NotNull(message = "fazendaId é obrigatório")
        Long fazendaId,

        @NotNull(message = "talhaoId é obrigatório")
        Long talhaoId,

        @NotBlank(message = "estagioFenologico é obrigatório")
        String estagioFenologico,

        @NotBlank(message = "nivelInfestacao é obrigatório")
        String nivelInfestacao,

        List<String> pragas,

        String observacoesTecnicas,

        String recomendacaoManejo,

        String latitude,

        String longitude,

        /** Foto em Base64 — opcional. Null se o agrônomo não fotografou. */
        String fotoBase64,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        LocalDateTime createdAt
) {}
