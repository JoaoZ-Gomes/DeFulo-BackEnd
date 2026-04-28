package com.defulo.api.features.usuario.mapper;

import org.springframework.stereotype.Component;

import com.defulo.api.features.usuario.dto.request.UsuarioCreateRequestDTO;
import com.defulo.api.features.usuario.dto.request.UsuarioUpdateRequestDTO;
import com.defulo.api.features.usuario.dto.response.UsuarioResponseDTO;
import com.defulo.api.features.usuario.model.Usuario;

/**
 * Mapper manual para Usuario. Seguindo o padrão PHteam para maior controle
 * sobre as transformações entre entidade e DTO.
 */
@Component
public class UsuarioMapper {

    /**
     * Converte DTO de criação para Entidade.
     */
    public Usuario toEntity(UsuarioCreateRequestDTO dto) {
        if (dto == null) return null;

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha()); // Senha será criptografada no Service
        usuario.setCpf(dto.cpf());
        usuario.setTelefone(dto.telefone());
        usuario.setPerfil(dto.perfil());
        usuario.setTalhaoId(dto.talhaoId());
        
        return usuario;
    }

    /**
     * Converte Entidade para DTO de resposta.
     */
    public UsuarioResponseDTO toResponseDTO(Usuario entity) {
        if (entity == null) return null;

        return new UsuarioResponseDTO(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getCpf(),
                entity.getTelefone(),
                entity.getPerfil(),
                entity.getTalhaoId(),
                entity.getDataCriacao(),
                entity.getDataAtualizacao()
        );
    }

    /**
     * Atualiza uma entidade existente com dados do DTO de update.
     * Campos nulos no DTO não sobrescrevem os valores atuais.
     */
    public void updateEntityFromDTO(UsuarioUpdateRequestDTO dto, Usuario entity) {
        if (dto == null || entity == null) return;

        if (dto.nome() != null) entity.setNome(dto.nome());
        if (dto.telefone() != null) entity.setTelefone(dto.telefone());
        if (dto.talhaoId() != null) entity.setTalhaoId(dto.talhaoId());
    }
}