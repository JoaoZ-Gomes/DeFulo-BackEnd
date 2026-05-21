package com.defulo.api.features.fazenda.mapper;

import com.defulo.api.features.fazenda.dto.response.FazendaResponseDTO;
import com.defulo.api.features.fazenda.model.Fazenda;
import org.springframework.stereotype.Component;

/**
 * Mapper manual para Fazenda.
 * Expõe dados do Produtor (id + nome) sem serializar a entidade completa.
 */
@Component
public class FazendaMapper {

    public FazendaResponseDTO toResponseDTO(Fazenda entity) {
        if (entity == null) return null;

        return new FazendaResponseDTO(
                entity.getId(),
                entity.getNome(),
                entity.getAreaTotal(),
                entity.getCultura(),
                entity.getProdutor().getId(),
                entity.getProdutor().getNome(),
                entity.getDataCriacao()
        );
    }
}
