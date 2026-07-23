package com.defulo.api.features.inspecao.dto.response;

import java.time.LocalDateTime;

/**
 * DTO de resposta para sincronização de inspeções fitossanitárias.
 *
 * <p>Retornado para cada item do lote recebido via
 * {@code POST /api/sync/inspecoes}. O app Flutter usa o {@code remoteId}
 * para atualizar o campo {@code idRemote} na tabela local e o
 * {@code status} para atualizar o {@code statusSync} do laudo.</p>
 *
 * @param localId   UUID do dispositivo — permite o app identificar qual
 *                  laudo local foi sincronizado
 * @param remoteId  ID gerado pelo servidor (null em caso de erro)
 * @param status    'SUCCESS' | 'CONFLICT' | 'ERROR'
 * @param message   Mensagem descritiva (ex: motivo do conflito ou erro)
 * @param syncedAt  Timestamp da sincronização no servidor
 */
public record InspecaoSyncResponseDTO(
        String localId,
        Long remoteId,
        String status,
        String message,
        LocalDateTime syncedAt
) {
    /** Factory para resposta de sucesso. */
    public static InspecaoSyncResponseDTO sucesso(String localId, Long remoteId) {
        return new InspecaoSyncResponseDTO(
                localId,
                remoteId,
                "SUCCESS",
                "Inspeção sincronizada com sucesso",
                LocalDateTime.now()
        );
    }

    /** Factory para resposta de item já sincronizado (idempotência). */
    public static InspecaoSyncResponseDTO jaExiste(String localId, Long remoteId) {
        return new InspecaoSyncResponseDTO(
                localId,
                remoteId,
                "SUCCESS",
                "Inspeção já sincronizada anteriormente",
                LocalDateTime.now()
        );
    }

    /** Factory para resposta de erro. */
    public static InspecaoSyncResponseDTO erro(String localId, String motivo) {
        return new InspecaoSyncResponseDTO(
                localId,
                null,
                "ERROR",
                motivo,
                LocalDateTime.now()
        );
    }
}
