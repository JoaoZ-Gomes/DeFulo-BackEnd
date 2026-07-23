package com.defulo.api.features.inspecao.model;

import com.defulo.api.features.fazenda.model.Fazenda;
import com.defulo.api.features.talhao.model.Talhao;
import com.defulo.api.features.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidade: Inspeção de Campo Fitossanitária.
 *
 * <p>Representa um laudo técnico gerado por um Engenheiro Agrônomo durante
 * uma visita de campo a um talhão. Contém os dados agronômicos de diagnóstico,
 * as coordenadas GPS coletadas via satélite (sem necessidade de internet) e
 * a referência à evidência fotográfica armazenada.</p>
 *
 * <p>Ciclo de vida do dado:</p>
 * <ol>
 *   <li>Criado localmente no app Flutter (offline, < 10ms)</li>
 *   <li>Enfileirado na SyncQueue com status PENDING</li>
 *   <li>Enviado ao servidor via POST /api/sync/inspecoes quando há internet</li>
 *   <li>Persistido aqui no PostgreSQL e retornado o remoteId ao dispositivo</li>
 * </ol>
 */
@Entity
@Table(
    name = "inspecoes_campo",
    indexes = {
        @Index(name = "idx_inspecao_talhao", columnList = "talhao_id"),
        @Index(name = "idx_inspecao_fazenda", columnList = "fazenda_id"),
        @Index(name = "idx_inspecao_engenheiro", columnList = "engenheiro_id"),
        @Index(name = "idx_inspecao_data", columnList = "data_inspecao"),
        @Index(name = "idx_inspecao_nivel", columnList = "nivel_infestacao"),
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspecaoCampo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -------------------------------------------------------------------------
    // DADOS AGRONÔMICOS
    // -------------------------------------------------------------------------

    /** Data e hora real da inspeção no campo (quando o agrônomo realizou). */
    @Column(name = "data_inspecao", nullable = false)
    private LocalDateTime dataInspecao;

    /**
     * Estágio fenológico da cultura no momento da inspeção.
     * Determinante para o limiar de dano econômico e janela de aplicação.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estagio_fenologico", nullable = false, length = 30)
    private EstagioFenologico estagioFenologico;

    /**
     * Nível de infestação/severidade detectado.
     * ALTO e CRÍTICO = nível de ação atingido → intervenção recomendada.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_infestacao", nullable = false, length = 20)
    private NivelInfestacao nivelInfestacao;

    /**
     * Lista de pragas e doenças detectadas, serializada como JSON.
     * Ex: ["Ferrugem Asiática","Lagarta-do-Cartucho"]
     *
     * Armazenada como TEXT (JSON) para facilitar queries de análise.
     */
    @Column(name = "pragas", columnDefinition = "TEXT")
    private String pragas;

    /**
     * Observações técnicas do agrônomo — descrição livre das condições
     * observadas: distribuição espacial, áreas críticas, clima, etc.
     */
    @Column(name = "observacoes_tecnicas", columnDefinition = "TEXT")
    private String observacoesTecnicas;

    /**
     * Prescrição técnica de manejo recomendada pelo agrônomo.
     * Inclui produto, dose, época de aplicação e cuidados.
     */
    @Column(name = "recomendacao_manejo", columnDefinition = "TEXT")
    private String recomendacaoManejo;

    // -------------------------------------------------------------------------
    // GEOLOCALIZAÇÃO (coletada via GPS/satélite — sem internet)
    // -------------------------------------------------------------------------

    /** Latitude decimal da inspeção (coletada por satélite). */
    @Column(name = "latitude", length = 20)
    private String latitude;

    /** Longitude decimal da inspeção (coletada por satélite). */
    @Column(name = "longitude", length = 20)
    private String longitude;

    // -------------------------------------------------------------------------
    // EVIDÊNCIA FOTOGRÁFICA
    // -------------------------------------------------------------------------

    /**
     * URL da foto de evidência armazenada no servidor.
     * Null até que a foto seja sincronizada do dispositivo.
     */
    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    // -------------------------------------------------------------------------
    // SINCRONIZAÇÃO (rastreabilidade offline-first)
    // -------------------------------------------------------------------------

    /**
     * UUID gerado pelo dispositivo móvel para idempotência.
     * Garante que o mesmo laudo não seja inserido duas vezes
     * mesmo que o dispositivo reenvie o payload.
     */
    @Column(name = "local_id", length = 36, unique = true)
    private String localId;

    /** Identificador do dispositivo que gerou o laudo. */
    @Column(name = "device_id", length = 255)
    private String deviceId;

    // -------------------------------------------------------------------------
    // RELACIONAMENTOS
    // -------------------------------------------------------------------------

    /** Talhão inspecionado. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talhao_id", nullable = false)
    private Talhao talhao;

    /** Fazenda à qual o talhão pertence (desnormalizado para performance). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fazenda_id", nullable = false)
    private Fazenda fazenda;

    /** Engenheiro Agrônomo responsável pela inspeção. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engenheiro_id", nullable = false)
    private Usuario engenheiro;

    // -------------------------------------------------------------------------
    // AUDITORIA
    // -------------------------------------------------------------------------

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
        if (this.dataInspecao == null) {
            this.dataInspecao = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
