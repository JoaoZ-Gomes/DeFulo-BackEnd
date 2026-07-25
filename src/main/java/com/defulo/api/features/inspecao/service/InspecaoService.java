package com.defulo.api.features.inspecao.service;

import com.defulo.api.features.fazenda.repository.FazendaRepository;
import com.defulo.api.features.inspecao.dto.request.InspecaoSyncRequestDTO;
import com.defulo.api.features.inspecao.dto.response.InspecaoSyncResponseDTO;
import com.defulo.api.features.inspecao.model.EstagioFenologico;
import com.defulo.api.features.inspecao.model.InspecaoCampo;
import com.defulo.api.features.inspecao.model.NivelInfestacao;
import com.defulo.api.features.inspecao.repository.InspecaoRepository;
import com.defulo.api.features.talhao.repository.TalhaoRepository;
import com.defulo.api.features.usuario.model.Perfil;
import com.defulo.api.features.usuario.model.Usuario;
import com.defulo.api.infrastructure.exception.RecursoNaoEncontradoException;
import com.defulo.api.infrastructure.security.AuthorizationService;
import com.defulo.api.infrastructure.storage.FileStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de Inspeções Fitossanitárias.
 *
 * <p>Responsabilidades principais:</p>
 * <ol>
 *   <li><b>Sync em Lote:</b> Processa múltiplos laudos de uma só vez para
 *       minimizar o número de roundtrips na sincronização</li>
 *   <li><b>Idempotência:</b> Verifica {@code localId} antes de inserir —
 *       o mesmo laudo pode ser reenviado N vezes sem criar duplicatas</li>
 *   <li><b>Autorização:</b> Apenas usuários com perfil ENGENHEIRO podem
 *       sincronizar e consultar inspeções fitossanitárias</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InspecaoService {

    private final InspecaoRepository inspecaoRepository;
    private final TalhaoRepository talhaoRepository;
    private final FazendaRepository fazendaRepository;
    private final AuthorizationService authorizationService;
    private final ObjectMapper objectMapper;
    private final FileStorageService fileStorageService;

    // -------------------------------------------------------------------------
    // SYNC: Receber Lote de Inspeções do App Móvel
    // -------------------------------------------------------------------------

    /**
     * Processa um lote de inspeções enviadas pelo dispositivo do engenheiro.
     *
     * <p>Para cada DTO no lote:</p>
     * <ol>
     *   <li>Verifica se o {@code localId} já existe (idempotência)</li>
     *   <li>Persiste a inspeção no PostgreSQL</li>
     *   <li>Processa e salva a foto se {@code fotoBase64} estiver presente</li>
     *   <li>Retorna o {@code remoteId} para o dispositivo atualizar seu banco local</li>
     * </ol>
     *
     * @param dtos     Lista de inspeções a sincronizar
     * @param deviceId Identificador do dispositivo de origem
     * @return Lista de respostas com status de cada item sincronizado
     */
    public List<InspecaoSyncResponseDTO> sincronizarLote(
            List<InspecaoSyncRequestDTO> dtos,
            String deviceId
    ) {
        // Validação de perfil: apenas ENGENHEIRO pode submeter inspeções
        authorizationService.exigirPerfil(Perfil.ENGENHEIRO);
        final Usuario engenheiro = authorizationService.getUsuarioAutenticado();

        log.info("🔄 [Sync] Iniciando sync de {} inspeções do dispositivo {}",
                dtos.size(), deviceId);

        final List<InspecaoSyncResponseDTO> resultados = new ArrayList<>();

        for (InspecaoSyncRequestDTO dto : dtos) {
            try {
                final InspecaoSyncResponseDTO resultado = processarItem(dto, engenheiro);
                resultados.add(resultado);
            } catch (Exception e) {
                // Falha em um item NÃO aborta o lote inteiro — resiliente por design
                log.error("❌ [Sync] Erro ao processar localId={}: {}",
                        dto.localId(), e.getMessage(), e);
                resultados.add(InspecaoSyncResponseDTO.erro(dto.localId(), e.getMessage()));
            }
        }

        log.info("✅ [Sync] Lote processado: {} sucessos, {} erros",
                resultados.stream().filter(r -> "SUCCESS".equals(r.status())).count(),
                resultados.stream().filter(r -> "ERROR".equals(r.status())).count());

        return resultados;
    }

    /**
     * Processa um único item do lote de sync.
     * Transacional individualmente para que falhas isoladas não afetem o lote.
     */
    @Transactional
    protected InspecaoSyncResponseDTO processarItem(
            InspecaoSyncRequestDTO dto,
            Usuario engenheiro
    ) {
        // ------------------------------------------------------------------
        // VERIFICAÇÃO DE IDEMPOTÊNCIA
        // Garante que múltiplos reenvios do mesmo laudo não criem duplicatas
        // ------------------------------------------------------------------
        final Optional<InspecaoCampo> existente = inspecaoRepository.findByLocalId(dto.localId());
        if (existente.isPresent()) {
            log.info("♻️ [Sync] localId={} já existe (remoteId={}) — idempotente",
                    dto.localId(), existente.get().getId());
            return InspecaoSyncResponseDTO.jaExiste(dto.localId(), existente.get().getId());
        }

        // ------------------------------------------------------------------
        // CONSTRUÇÃO DA ENTIDADE
        // ------------------------------------------------------------------
        final var talhao = talhaoRepository.findById(dto.talhaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Talhão não encontrado: " + dto.talhaoId()));

        final var fazenda = fazendaRepository.findById(dto.fazendaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Fazenda não encontrada: " + dto.fazendaId()));

        // Converte string do Flutter para enum Java (tolerante a case)
        final EstagioFenologico estagio = EstagioFenologico.valueOf(
                dto.estagioFenologico().toUpperCase().replace(" ", "_")
        );
        final NivelInfestacao nivel = NivelInfestacao.valueOf(
                dto.nivelInfestacao().toUpperCase()
        );

        // Serializa lista de pragas de volta para JSON string (armazenamento no banco)
        String pragasJson = "[]";
        try {
            if (dto.pragas() != null && !dto.pragas().isEmpty()) {
                pragasJson = objectMapper.writeValueAsString(dto.pragas());
            }
        } catch (Exception e) {
            log.warn("⚠️ [Sync] Erro ao serializar pragas para localId={}: {}",
                    dto.localId(), e.getMessage());
        }

        final InspecaoCampo inspecao = new InspecaoCampo();
        inspecao.setLocalId(dto.localId());
        inspecao.setDeviceId(dto.deviceId() != null ? dto.deviceId() : "unknown");
        inspecao.setTalhao(talhao);
        inspecao.setFazenda(fazenda);
        inspecao.setEngenheiro(engenheiro);
        inspecao.setEstagioFenologico(estagio);
        inspecao.setNivelInfestacao(nivel);
        inspecao.setPragas(pragasJson);
        inspecao.setObservacoesTecnicas(dto.observacoesTecnicas());
        inspecao.setRecomendacaoManejo(dto.recomendacaoManejo());
        inspecao.setLatitude(dto.latitude());
        inspecao.setLongitude(dto.longitude());

        // Usa o timestamp do dispositivo como data de inspeção (ocorreu offline)
        inspecao.setDataInspecao(
                dto.createdAt() != null ? dto.createdAt() : LocalDateTime.now()
        );

        // ------------------------------------------------------------------
        // PROCESSAMENTO DA FOTO (se enviada)
        // ------------------------------------------------------------------
        if (dto.fotoBase64() != null && !dto.fotoBase64().isBlank()) {
            final String fotoUrl = salvarFotoBase64(dto.fotoBase64(), dto.localId());
            inspecao.setFotoUrl(fotoUrl);
        }

        // ------------------------------------------------------------------
        // PERSISTÊNCIA
        // ------------------------------------------------------------------
        final InspecaoCampo salva = inspecaoRepository.save(inspecao);
        log.info("✅ [Sync] Inspeção salva: localId={} → remoteId={}",
                dto.localId(), salva.getId());

        // Alerta de nível crítico no log — visível no monitoramento do servidor
        if (nivel.exigeAcaoImediata()) {
            log.warn("🚨 [Fitossanidade] NÍVEL DE AÇÃO: localId={}, talhão={}, nível={}",
                    dto.localId(), dto.talhaoId(), nivel);
        }

        return InspecaoSyncResponseDTO.sucesso(dto.localId(), salva.getId());
    }

    // -------------------------------------------------------------------------
    // CONSULTAS
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public Page<InspecaoCampo> listarPorTalhao(Long talhaoId, Pageable pageable) {
        authorizationService.exigirPerfil(Perfil.ENGENHEIRO);
        final var talhao = talhaoRepository.findById(talhaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Talhão não encontrado: " + talhaoId));
        authorizationService.exigirAcessoATalhao(talhao);
        return inspecaoRepository.findByTalhaoIdOrderByDataInspecaoDesc(talhaoId, pageable);
    }

    // -------------------------------------------------------------------------
    // UTILITÁRIOS PRIVADOS
    // -------------------------------------------------------------------------

    /**
     * Salva uma foto enviada em Base64 em disco local e retorna a URL de acesso.
     *
     * <p>Nota: para múltiplas instâncias do backend atrás de um load balancer,
     * trocar {@link FileStorageService} por um backend de objeto (S3/GCS/MinIO)
     * mantendo a mesma assinatura.</p>
     *
     * @param base64  Foto codificada em Base64
     * @param localId UUID do laudo para nomear o arquivo
     * @return URL de acesso à foto
     */
    private String salvarFotoBase64(String base64, String localId) {
        String url = fileStorageService.salvarImagemBase64(base64, "inspecoes", localId);
        log.info("📷 [Foto] Foto salva para localId={} em {}", localId, url);
        return url;
    }
}
