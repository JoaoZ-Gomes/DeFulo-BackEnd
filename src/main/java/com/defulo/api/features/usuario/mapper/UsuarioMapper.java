package com.defulo.api.features.usuario.mapper;

import com.defulo.api.features.usuario.dto.request.UsuarioCreateRequestDTO;
import com.defulo.api.features.usuario.dto.request.UsuarioUpdateRequestDTO;
import com.defulo.api.features.usuario.dto.response.UsuarioResponseDTO;
import com.defulo.api.features.usuario.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper para Usuario base.
 * Usa NullValuePropertyMappingStrategy.IGNORE para suportar partial updates.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UsuarioMapper {

    @Mapping(target = "id",              ignore = true)
    @Mapping(target = "perfil",          ignore = true) // definido pela subclasse
    @Mapping(target = "talhaoId",        ignore = true)
    @Mapping(target = "dataCriacao",     ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    Usuario toEntity(UsuarioCreateRequestDTO dto);

    UsuarioResponseDTO toResponseDTO(Usuario entity);

    @Mapping(target = "id",              ignore = true)
    @Mapping(target = "email",           ignore = true)
    @Mapping(target = "senha",           ignore = true)
    @Mapping(target = "cpf",             ignore = true)
    @Mapping(target = "perfil",          ignore = true)
    @Mapping(target = "talhaoId",        ignore = true)
    @Mapping(target = "dataCriacao",     ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    void updateEntityFromDTO(UsuarioUpdateRequestDTO dto, @MappingTarget Usuario entity);
}