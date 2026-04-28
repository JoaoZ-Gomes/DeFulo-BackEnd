package com.defulo.api.features.rtv.model;

import com.defulo.api.features.usuario.model.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entidade que representa o perfil RTV (Representante Técnico de Vendas).
 * Estende Usuario e é armazenada na tabela única 'usuarios' via discriminador.
 */
@Entity
@DiscriminatorValue("RTV")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Rtv extends Usuario {

    @Column(length = 100)
    private String regiao;

    @Column(name = "codigo_rtv", length = 50)
    private String codigoRtv;
}
