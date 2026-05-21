package com.defulo.api.features.talhao.mapper;

import com.defulo.api.features.talhao.dto.response.TalhaoResponseDTO;
import com.defulo.api.features.talhao.model.Talhao;
import org.springframework.stereotype.Component;

/**
 * Mapper manual para Talhão.
 * Expõe dados da Fazenda associada sem serializar a entidade inteira.
 */
@Component
public class TalhaoMapper {

    public TalhaoResponseDTO toResponseDTO(Talhao entity) {
        if (entity == null) return null;

        return new TalhaoResponseDTO(
                entity.getId(),
                entity.getNumero(),
                entity.getArea(),
                entity.getCultura(),
                entity.getDataPlantio(),
                entity.getLimiteCriticoUmidade(),
                entity.getFazenda().getId(),
                entity.getFazenda().getNome(),
                entity.getDataCriacao()
        );
    }
}
