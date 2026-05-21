package com.defulo.api.features.evento.model;


import com.defulo.api.features.rtv.model.Rtv;
import com.defulo.api.features.talhao.model.Talhao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_manejo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoManejo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @Column(nullable = false)
    private LocalDateTime data;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEvento tipo;

    @Column(nullable = false, length = 100)
    private String nome;

    private String descricao;
     private String quantidade;

     @ManyToOne
    @JoinColumn(name = "talhao_id")
    private Talhao talhao;

    @ManyToOne
    @JoinColumn(name = "rtv_id")
    private Rtv rtv;




}
