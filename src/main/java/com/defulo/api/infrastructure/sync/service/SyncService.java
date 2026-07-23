package com.defulo.api.infrastructure.sync.service;

import com.defulo.api.features.evento.model.EventoManejo;
import com.defulo.api.features.evento.model.TipoEvento;
import com.defulo.api.features.evento.repository.EventoRepository;
import com.defulo.api.features.fazenda.model.Fazenda;
import com.defulo.api.features.fazenda.repository.FazendaRepository;
import com.defulo.api.features.produtor.model.Produtor;
import com.defulo.api.features.produtor.repository.ProdutorRepository;
import com.defulo.api.features.talhao.model.Talhao;
import com.defulo.api.features.talhao.repository.TalhaoRepository;
import com.defulo.api.infrastructure.exception.RecursoNaoEncontradoException;
import com.defulo.api.infrastructure.sync.dto.*;
import com.defulo.api.infrastructure.sync.model.SyncIdMapping;
import com.defulo.api.infrastructure.sync.repository.SyncIdMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço de sincronização offline-first.
 *
 * Responsável por:
 * 1. Processar operações em lote enviadas pelo cliente (push)
 * 2. Garantir idempotência usando a tabela sync_id_mapping
 * 3. Fornecer dados modificados desde um timestamp (pull incremental)
 * 4. Registrar mapeamento local-ID → remote-ID para cada entidade criada offline
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SyncService {

    private final FazendaRepository fazendaRepository;
    private final TalhaoRepository talhaoRepository;
    private final EventoRepository eventoRepository;
    private final ProdutorRepository produtorRepository;
    private final SyncIdMappingRepository syncIdMappingRepository;

    // ─── CONSTANTES DE TIPO ────────────────────────────────────────────────────

    private static final String ENTITY_FAZENDA = "FAZENDA";
    private static final String ENTITY_TALHAO  = "TALHAO";
    private static final String ENTITY_EVENTO  = "EVENTO";

    // ═══════════════════════════════════════════════════════════════════════════
    // PUSH SYNC
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Processa um lote de operações enviadas pelo cliente (push sync).
     * Cada item é processado individualmente com:
     *  1. Verificação de idempotência
     *  2. Validação básica
     *  3. Execução (CREATE / UPDATE / DELETE)
     *  4. Registro do mapeamento local→remoto
     */
    @Transactional
    public SyncPushResponseDTO processPushSync(SyncPushRequestDTO request) {
        String syncId   = UUID.randomUUID().toString();
        String deviceId = request.getDeviceId();
        List<SyncItemDTO> items = request.getItems();

        log.info("Push sync {} | {} itens | dispositivo {}", syncId, items.size(), deviceId);

        List<SyncItemResponseDTO> responses = items.stream()
                .map(item -> processSyncItem(item, deviceId, syncId))
                .collect(Collectors.toList());

        long successCount  = responses.stream().filter(r -> "SUCCESS".equals(r.getStatus())).count();
        long conflictCount = responses.stream().filter(r -> "CONFLICT".equals(r.getStatus())).count();
        long errorCount    = responses.stream().filter(r -> "ERROR".equals(r.getStatus())).count();

        SyncPushResponseDTO response = new SyncPushResponseDTO();
        response.setSyncId(syncId);
        response.setItems(responses);
        response.setTotalProcessed(items.size());
        response.setSuccessCount((int) successCount);
        response.setConflictCount((int) conflictCount);
        response.setErrorCount((int) errorCount);

        log.info("Push sync {} concluído | sucesso={} conflito={} erro={}", syncId, successCount, conflictCount, errorCount);
        return response;
    }

    /**
     * Processa um único item do lote de sincronização.
     */
    private SyncItemResponseDTO processSyncItem(SyncItemDTO item, String deviceId, String syncId) {
        SyncItemResponseDTO response = new SyncItemResponseDTO();
        response.setLocalId(item.getLocalId());

        try {
            // 1. Idempotência: item já processado anteriormente?
            Optional<SyncIdMapping> existingMapping = syncIdMappingRepository
                    .findByLocalIdAndDeviceIdAndEntityType(item.getLocalId(), deviceId, item.getEntityType().toUpperCase());

            if (existingMapping.isPresent()) {
                log.debug("Item {} já processado — idempotência aplicada", item.getLocalId());
                response.setStatus("SUCCESS");
                response.setRemoteId(existingMapping.get().getRemoteId());
                return response;
            }

            // 2. Validação básica
            if (!validateItemData(item)) {
                log.warn("Dados inválidos para item {}", item.getLocalId());
                response.setStatus("ERROR");
                response.setErrorMessage("Dados inválidos ou incompletos");
                return response;
            }

            // 3. Executar operação
            Long remoteId = executeOperation(item, deviceId, syncId);

            response.setStatus("SUCCESS");
            response.setRemoteId(remoteId);

            // 4. Registrar mapeamento localId → remoteId
            if ("CREATE".equalsIgnoreCase(item.getOperation()) && remoteId != null) {
                recordIdMapping(item.getLocalId(), remoteId, item.getEntityType().toUpperCase(), deviceId, syncId);
            }

        } catch (Exception e) {
            log.error("Erro ao processar item {} ({}): {}", item.getLocalId(), item.getEntityType(), e.getMessage(), e);
            response.setStatus("ERROR");
            response.setErrorMessage(e.getMessage());
        }

        return response;
    }

    // ─── VALIDAÇÃO ─────────────────────────────────────────────────────────────

    private boolean validateItemData(SyncItemDTO item) {
        if (item.getLocalId()    == null || item.getLocalId().isBlank())    return false;
        if (item.getEntityType() == null || item.getEntityType().isBlank()) return false;
        if (item.getOperation()  == null || item.getOperation().isBlank())  return false;
        if (item.getPayload()    == null || item.getPayload().isEmpty())     return false;
        if (item.getChecksum()   == null || item.getChecksum().isBlank())   return false;
        return isValidOperation(item.getOperation());
    }

    private boolean isValidOperation(String op) {
        return "CREATE".equalsIgnoreCase(op) || "UPDATE".equalsIgnoreCase(op) || "DELETE".equalsIgnoreCase(op);
    }

    // ─── ROTEAMENTO DE OPERAÇÃO ────────────────────────────────────────────────

    private Long executeOperation(SyncItemDTO item, String deviceId, String syncId) {
        String entity    = item.getEntityType().toUpperCase();
        String operation = item.getOperation().toUpperCase();
        Map<String, Object> payload = item.getPayload();

        return switch (operation) {
            case "CREATE" -> executeCreate(entity, payload, item.getLocalId(), deviceId);
            case "UPDATE" -> executeUpdate(entity, payload, item.getLocalId(), deviceId);
            case "DELETE" -> { executeDelete(entity, payload, item.getLocalId(), deviceId); yield null; }
            default -> throw new IllegalArgumentException("Operação desconhecida: " + operation);
        };
    }

    // ─── CREATE ────────────────────────────────────────────────────────────────

    private Long executeCreate(String entityType, Map<String, Object> payload, String localId, String deviceId) {
        log.info("CREATE {} | localId={}", entityType, localId);
        return switch (entityType) {
            case ENTITY_FAZENDA -> createFazenda(payload);
            case ENTITY_TALHAO  -> createTalhao(payload, deviceId);
            case ENTITY_EVENTO  -> createEvento(payload, deviceId);
            default -> throw new IllegalArgumentException("Tipo de entidade desconhecido para CREATE: " + entityType);
        };
    }

    private Long createFazenda(Map<String, Object> payload) {
        Long produtorId = getLong(payload, "produtorId");
        Produtor produtor = produtorRepository.findById(produtorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produtor não encontrado: " + produtorId));

        Fazenda fazenda = new Fazenda();
        fazenda.setNome(getString(payload, "nome"));
        fazenda.setAreaTotal(getBigDecimal(payload, "areaTotal"));
        fazenda.setCultura(getString(payload, "cultura"));
        fazenda.setProdutor(produtor);

        return fazendaRepository.save(fazenda).getId();
    }

    private Long createTalhao(Map<String, Object> payload, String deviceId) {
        // fazendaId pode vir como ID remoto direto ou precisar de mapeamento
        Long fazendaId = resolveFazendaId(payload, deviceId);
        Fazenda fazenda = fazendaRepository.findById(fazendaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fazenda não encontrada: " + fazendaId));

        Talhao talhao = new Talhao();
        talhao.setNumero(getString(payload, "numero"));
        talhao.setArea(getDouble(payload, "area"));
        talhao.setCultura(getString(payload, "cultura"));
        talhao.setFazenda(fazenda);
        if (payload.get("limiteCriticoUmidade") != null) {
            talhao.setLimiteCriticoUmidade(getDouble(payload, "limiteCriticoUmidade"));
        }

        return talhaoRepository.save(talhao).getId();
    }

    private Long createEvento(Map<String, Object> payload, String deviceId) {
        Long talhaoId = resolveTalhaoId(payload, deviceId);
        Talhao talhao = talhaoRepository.findById(talhaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Talhão não encontrado: " + talhaoId));

        EventoManejo evento = new EventoManejo();
        evento.setNome(getString(payload, "nome"));
        evento.setDescricao(getString(payload, "descricao"));
        evento.setTipo(TipoEvento.valueOf(getString(payload, "tipo")));
        evento.setData(LocalDateTime.now());
        evento.setTalhao(talhao);
        if (payload.get("quantidadeValor") != null) {
            evento.setQuantidadeValor(getDouble(payload, "quantidadeValor"));
        }
        if (payload.get("quantidadeUnidade") != null) {
            evento.setQuantidadeUnidade(getString(payload, "quantidadeUnidade"));
        }

        return eventoRepository.save(evento).getId();
    }

    // ─── UPDATE ────────────────────────────────────────────────────────────────

    private Long executeUpdate(String entityType, Map<String, Object> payload, String localId, String deviceId) {
        log.info("UPDATE {} | localId={}", entityType, localId);
        return switch (entityType) {
            case ENTITY_FAZENDA -> updateFazenda(payload);
            case ENTITY_TALHAO  -> updateTalhao(payload);
            case ENTITY_EVENTO  -> updateEvento(payload);
            default -> throw new IllegalArgumentException("Tipo de entidade desconhecido para UPDATE: " + entityType);
        };
    }

    private Long updateFazenda(Map<String, Object> payload) {
        Long id = getLong(payload, "id");
        Fazenda fazenda = fazendaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fazenda não encontrada: " + id));

        if (payload.get("nome")      != null) fazenda.setNome(getString(payload, "nome"));
        if (payload.get("areaTotal") != null) fazenda.setAreaTotal(getBigDecimal(payload, "areaTotal"));
        if (payload.get("cultura")   != null) fazenda.setCultura(getString(payload, "cultura"));

        return fazendaRepository.save(fazenda).getId();
    }

    private Long updateTalhao(Map<String, Object> payload) {
        Long id = getLong(payload, "id");
        Talhao talhao = talhaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Talhão não encontrado: " + id));

        if (payload.get("numero")               != null) talhao.setNumero(getString(payload, "numero"));
        if (payload.get("area")                 != null) talhao.setArea(getDouble(payload, "area"));
        if (payload.get("cultura")              != null) talhao.setCultura(getString(payload, "cultura"));
        if (payload.get("limiteCriticoUmidade") != null) talhao.setLimiteCriticoUmidade(getDouble(payload, "limiteCriticoUmidade"));

        return talhaoRepository.save(talhao).getId();
    }

    private Long updateEvento(Map<String, Object> payload) {
        Long id = getLong(payload, "id");
        EventoManejo evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento não encontrado: " + id));

        if (payload.get("nome")              != null) evento.setNome(getString(payload, "nome"));
        if (payload.get("descricao")         != null) evento.setDescricao(getString(payload, "descricao"));
        if (payload.get("quantidadeValor")   != null) evento.setQuantidadeValor(getDouble(payload, "quantidadeValor"));
        if (payload.get("quantidadeUnidade") != null) evento.setQuantidadeUnidade(getString(payload, "quantidadeUnidade"));

        return eventoRepository.save(evento).getId();
    }

    // ─── DELETE ────────────────────────────────────────────────────────────────

    private void executeDelete(String entityType, Map<String, Object> payload, String localId, String deviceId) {
        log.info("DELETE {} | localId={}", entityType, localId);
        Long id = getLong(payload, "id");
        switch (entityType) {
            case ENTITY_FAZENDA -> fazendaRepository.deleteById(id);
            case ENTITY_TALHAO  -> talhaoRepository.deleteById(id);
            case ENTITY_EVENTO  -> eventoRepository.deleteById(id);
            default -> throw new IllegalArgumentException("Tipo de entidade desconhecido para DELETE: " + entityType);
        }
    }

    // ─── MAPEAMENTO LOCAL → REMOTO ─────────────────────────────────────────────

    private void recordIdMapping(String localId, Long remoteId, String entityType, String deviceId, String syncId) {
        SyncIdMapping mapping = new SyncIdMapping();
        mapping.setLocalId(localId);
        mapping.setRemoteId(remoteId);
        mapping.setEntityType(entityType);
        mapping.setDeviceId(deviceId);
        mapping.setSyncId(syncId);
        syncIdMappingRepository.save(mapping);
        log.debug("Mapeamento registrado: {} → {} ({} | device={})", localId, remoteId, entityType, deviceId);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PULL SYNC — dados incrementais desde um timestamp
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Retorna todos os registros modificados após o timestamp informado.
     * O cliente usa o serverTimestamp retornado como próximo valor de "since".
     */
    @Transactional(readOnly = true)
    public SyncPullResponseDTO performPullSync(String since) {
        log.info("Pull sync | desde: {}", since);

        LocalDateTime sinceTime = OffsetDateTime.parse(since).toLocalDateTime();
        List<SyncPullItemDTO> items = new ArrayList<>();

        // Fazendas modificadas
        fazendaRepository.findByDataAtualizacaoAfter(sinceTime)
                .forEach(f -> items.add(toSyncPullItem(f)));

        // Talhões modificados
        talhaoRepository.findByDataAtualizacaoAfter(sinceTime)
                .forEach(t -> items.add(toSyncPullItem(t)));

        // Eventos modificados
        eventoRepository.findByDataAtualizacaoAfter(sinceTime)
                .forEach(e -> items.add(toSyncPullItem(e)));

        SyncPullResponseDTO response = new SyncPullResponseDTO();
        response.setItems(items);
        response.setServerTimestamp(OffsetDateTime.now().toString());

        log.info("Pull sync retornando {} itens", items.size());
        return response;
    }

    private SyncPullItemDTO toSyncPullItem(Fazenda f) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id",           f.getId());
        data.put("nome",         f.getNome());
        data.put("areaTotal",    f.getAreaTotal());
        data.put("cultura",      f.getCultura());
        data.put("produtorId",   f.getProdutor().getId());
        data.put("dataCriacao",  f.getDataCriacao() != null ? f.getDataCriacao().toString() : null);

        SyncPullItemDTO item = new SyncPullItemDTO();
        item.setRemoteId(f.getId());
        item.setEntityType(ENTITY_FAZENDA);
        item.setOperation("UPDATE");
        item.setData(data);
        item.setUpdatedAt(f.getDataAtualizacao() != null ? f.getDataAtualizacao().toString() : null);
        return item;
    }

    private SyncPullItemDTO toSyncPullItem(Talhao t) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id",                    t.getId());
        data.put("numero",                t.getNumero());
        data.put("area",                  t.getArea());
        data.put("cultura",               t.getCultura());
        data.put("fazendaId",             t.getFazenda().getId());
        data.put("limiteCriticoUmidade",  t.getLimiteCriticoUmidade());
        data.put("dataPlantio",           t.getDataPlantio() != null ? t.getDataPlantio().toString() : null);
        data.put("dataCriacao",           t.getDataCriacao() != null ? t.getDataCriacao().toString() : null);

        SyncPullItemDTO item = new SyncPullItemDTO();
        item.setRemoteId(t.getId());
        item.setEntityType(ENTITY_TALHAO);
        item.setOperation("UPDATE");
        item.setData(data);
        item.setUpdatedAt(t.getDataAtualizacao() != null ? t.getDataAtualizacao().toString() : null);
        return item;
    }

    private SyncPullItemDTO toSyncPullItem(EventoManejo e) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id",                  e.getId());
        data.put("nome",                e.getNome());
        data.put("descricao",           e.getDescricao());
        data.put("tipo",                e.getTipo() != null ? e.getTipo().name() : null);
        data.put("data",                e.getData() != null ? e.getData().toString() : null);
        data.put("talhaoId",            e.getTalhao() != null ? e.getTalhao().getId() : null);
        data.put("rtvId",               e.getRtv()    != null ? e.getRtv().getId()    : null);
        data.put("quantidadeValor",     e.getQuantidadeValor());
        data.put("quantidadeUnidade",   e.getQuantidadeUnidade());

        SyncPullItemDTO item = new SyncPullItemDTO();
        item.setRemoteId(e.getId());
        item.setEntityType(ENTITY_EVENTO);
        item.setOperation("UPDATE");
        item.setData(data);
        item.setUpdatedAt(e.getDataAtualizacao() != null ? e.getDataAtualizacao().toString() : null);
        return item;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // STATUS DE SYNC
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Retorna o status de sincronização para um dispositivo.
     * Inclui o último syncId processado com sucesso e timestamp.
     */
    @Transactional(readOnly = true)
    public SyncStatusDTO getSyncStatus(String deviceId) {
        log.debug("Status de sync | deviceId={}", deviceId);

        SyncStatusDTO status = new SyncStatusDTO();
        status.setDeviceId(deviceId);

        // Buscar o registro mais recente deste dispositivo
        syncIdMappingRepository.findAll().stream()
                .filter(m -> deviceId.equals(m.getDeviceId()))
                .max(Comparator.comparing(SyncIdMapping::getCriadoEm))
                .ifPresentOrElse(
                    latest -> {
                        status.setLastSyncId(latest.getSyncId());
                        status.setLastSyncTimestamp(latest.getCriadoEm().toString());
                        status.setLastSyncStatus("DONE");
                    },
                    () -> {
                        status.setLastSyncStatus("NEVER");
                        status.setPendingItemsCount(0);
                    }
                );

        return status;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // RESOLUÇÃO DE IDs — suporte a FKs que vieram como localId offline
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Resolve o ID remoto de uma fazenda.
     * O payload pode conter "fazendaId" (remoto) ou "fazendaLocalId" (local, mapeado).
     */
    private Long resolveFazendaId(Map<String, Object> payload, String deviceId) {
        if (payload.get("fazendaId") != null) {
            return getLong(payload, "fazendaId");
        }
        String fazendaLocalId = getString(payload, "fazendaLocalId");
        return syncIdMappingRepository
                .findByLocalIdAndDeviceIdAndEntityType(fazendaLocalId, deviceId, ENTITY_FAZENDA)
                .map(SyncIdMapping::getRemoteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Mapeamento de fazendaLocalId não encontrado: " + fazendaLocalId));
    }

    /**
     * Resolve o ID remoto de um talhão.
     * O payload pode conter "talhaoId" (remoto) ou "talhaoLocalId" (local, mapeado).
     */
    private Long resolveTalhaoId(Map<String, Object> payload, String deviceId) {
        if (payload.get("talhaoId") != null) {
            return getLong(payload, "talhaoId");
        }
        String talhaoLocalId = getString(payload, "talhaoLocalId");
        return syncIdMappingRepository
                .findByLocalIdAndDeviceIdAndEntityType(talhaoLocalId, deviceId, ENTITY_TALHAO)
                .map(SyncIdMapping::getRemoteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Mapeamento de talhaoLocalId não encontrado: " + talhaoLocalId));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // HELPERS DE PAYLOAD
    // ═══════════════════════════════════════════════════════════════════════════

    private String getString(Map<String, Object> payload, String key) {
        Object val = payload.get(key);
        return val != null ? val.toString() : null;
    }

    private Long getLong(Map<String, Object> payload, String key) {
        Object val = payload.get(key);
        if (val == null) throw new IllegalArgumentException("Campo obrigatório ausente no payload: " + key);
        if (val instanceof Number n) return n.longValue();
        return Long.parseLong(val.toString());
    }

    private Double getDouble(Map<String, Object> payload, String key) {
        Object val = payload.get(key);
        if (val == null) return null;
        if (val instanceof Number n) return n.doubleValue();
        return Double.parseDouble(val.toString());
    }

    private java.math.BigDecimal getBigDecimal(Map<String, Object> payload, String key) {
        Double val = getDouble(payload, key);
        return val != null ? java.math.BigDecimal.valueOf(val) : null;
    }
}
