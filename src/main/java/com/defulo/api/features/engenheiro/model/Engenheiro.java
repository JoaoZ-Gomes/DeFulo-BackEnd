package com.defulo.api.features.engenheiro.model;

import com.defulo.api.features.usuario.model.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entidade que representa o perfil Engenheiro Agrônomo.
 */
@Entity
@DiscriminatorValue("ENGENHEIRO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Engenheiro extends Usuario {

    @Column(length = 150)
    private String especialidade;
}