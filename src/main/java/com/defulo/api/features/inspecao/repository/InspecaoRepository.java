package com.defulo.api.features.inspecao.repository;

import com.defulo.api.features.inspecao.model.InspecaoCampo;
import com.defulo.api.features.inspecao.model.NivelInfestacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository JPA para Inspeções de Campo.
 *
 * <p>Consultas chave:</p>
 * <ul>
 *   <li>{@link #findByLocalId} — verificação de idempotência no sync</li>
 *   <li>{@link #findByTalhaoId} — histórico de laudos de um talhão</li>
 *   <li>{@link #findByFazendaId} — visão consolidada da fazenda</li>
 *   <li>{@link #countNivelAcaoByFazenda} — alerta de fazendas críticas</li>
 * </ul>
 */
@Repository
public interface InspecaoRepository extends JpaRepository<InspecaoCampo, Long> {

    /**
     * Busca uma inspeção pelo UUID do dispositivo.
     * Usado para verificação de idempotência durante o recebimento de sync:
     * se o localId já existe, não cria um novo registro.
     *
     * @param localId UUID v4 gerado pelo app Flutter
     */
    Optional<InspecaoCampo> findByLocalId(String localId);

    /**
     * Lista todas as inspeções de um talhão, ordenadas por data (mais recentes primeiro).
     *
     * @param talhaoId ID do talhão
     * @param pageable paginação e ordenação
     */
    Page<InspecaoCampo> findByTalhaoIdOrderByDataInspecaoDesc(Long talhaoId, Pageable pageable);

    /**
     * Lista todas as inspeções de uma fazenda, ordenadas por data (mais recentes primeiro).
     * Útil para o dashboard de monitoramento da fazenda.
     *
     * @param fazendaId ID da fazenda
     * @param pageable  paginação e ordenação
     */
    Page<InspecaoCampo> findByFazendaIdOrderByDataInspecaoDesc(Long fazendaId, Pageable pageable);

    /**
     * Conta inspeções com nível de ação atingido (ALTO ou CRITICO) em uma fazenda.
     * Usado para alertas no dashboard e relatórios gerenciais.
     *
     * @param fazendaId     ID da fazenda
     * @param nivelCritico  Nível de infestação (CRITICO)
     * @param nivelAlto     Nível de infestação (ALTO)
     */
    @Query("SELECT COUNT(i) FROM InspecaoCampo i " +
           "WHERE i.fazenda.id = :fazendaId " +
           "AND i.nivelInfestacao IN (:nivelAlto, :nivelCritico)")
    long countNivelAcaoByFazenda(
            @Param("fazendaId") Long fazendaId,
            @Param("nivelAlto") NivelInfestacao nivelAlto,
            @Param("nivelCritico") NivelInfestacao nivelCritico
    );
}
