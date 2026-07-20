package com.defulo.api.features.talhao.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.defulo.api.features.fazenda.model.Fazenda;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "talhoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Talhao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String numero;

    @Column(nullable = false)
    private Double area;

    @Column(nullable = false, length = 50)
    private String cultura;

    @Column(name = "data_plantio")
    private LocalDate dataPlantio;

    @Column(name = "limite_critico_umidade")
    private Double limiteCriticoUmidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fazenda_id", nullable = false)
    private Fazenda fazenda;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
}
