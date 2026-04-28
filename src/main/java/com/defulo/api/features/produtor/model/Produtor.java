package com.defulo.api.features.produtor.model;

import com.defulo.api.features.usuario.model.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entidade que representa o perfil Produtor.
 */
@Entity
@DiscriminatorValue("PRODUTOR")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Produtor extends Usuario {

    @Column(length = 150)
    private String propriedade;

    @Column(name = "area_total")
    private Double areaTotal;
}
