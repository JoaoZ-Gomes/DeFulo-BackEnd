package com.defulo.api.features.engenheiro.mapper;

import org.springframework.stereotype.Component;

import com.defulo.api.features.engenheiro.dto.request.EngenheiroCreateRequestDTO;
import com.defulo.api.features.engenheiro.dto.response.EngenheiroResponseDTO;
import com.defulo.api.features.engenheiro.model.Engenheiro;
import com.defulo.api.features.usuario.model.Perfil;

@Component
public class EngenheiroMapper {

    public Engenheiro toEntity(EngenheiroCreateRequestDTO dto) {
        if (dto == null) return null;

        Engenheiro entity = new Engenheiro();
        entity.setNome(dto.nome());
        entity.setEmail(dto.email());
        entity.setSenha(dto.senha());
        entity.setCpf(dto.cpf());
        entity.setTelefone(dto.telefone());
        entity.setEspecialidade(dto.especialidade());
        entity.setPerfil(Perfil.ENGENHEIRO);
        
        return entity;
    }

    public EngenheiroResponseDTO toResponseDTO(Engenheiro entity) {
        if (entity == null) return null;

        return new EngenheiroResponseDTO(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getCpf(),
                entity.getTelefone(),
                entity.getEspecialidade(),
                entity.getDataCriacao(),
                entity.getDataAtualizacao()
        );
    }
}