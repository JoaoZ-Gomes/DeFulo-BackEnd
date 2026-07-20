package com.defulo.api.features.evento.mapper;

import com.defulo.api.features.evento.dto.request.EventoCreateRequestDto;
import com.defulo.api.features.evento.dto.request.EventoUpdateDTO;
import com.defulo.api.features.evento.dto.response.EventoResponseDTO;
import com.defulo.api.features.evento.model.EventoManejo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper para EventoManejo.
 * talhao e rtv são gerenciados pelo serviço (lookup por ID).
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface EventoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "data", ignore = true)          // definido pelo service: LocalDateTime.now()
    @Mapping(target = "talhao", ignore = true)        // lookup por talhaoId feito no service
    @Mapping(target = "rtv", ignore = true)           // RTV vem do usuário autenticado
    @Mapping(target = "dataAtualizacao", ignore = true)
    EventoManejo toEntity(EventoCreateRequestDto dto);

    @Mapping(target = "talhaoId",        source = "talhao.id")
    @Mapping(target = "talhaoNumero",    source = "talhao.numero")
    @Mapping(target = "rtvId",           source = "rtv.id")
    @Mapping(target = "rtvNome",         source = "rtv.nome")
    EventoResponseDTO toResponseDTO(EventoManejo entity);

    /** Atualização parcial: apenas nome, descrição e quantidade são mutáveis. */
    @Mapping(target = "id",                ignore = true)
    @Mapping(target = "data",              ignore = true)
    @Mapping(target = "tipo",              ignore = true)
    @Mapping(target = "talhao",            ignore = true)
    @Mapping(target = "rtv",              ignore = true)
    @Mapping(target = "dataAtualizacao",  ignore = true)
    void updateEntityFromDTO(EventoUpdateDTO dto, @MappingTarget EventoManejo entity);
}
