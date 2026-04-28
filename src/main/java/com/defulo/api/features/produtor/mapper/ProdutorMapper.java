package com.defulo.api.features.produtor.mapper;

import org.springframework.stereotype.Component;

import com.defulo.api.features.produtor.dto.request.ProdutorCreateRequestDTO;
import com.defulo.api.features.produtor.dto.response.ProdutorResponseDTO;
import com.defulo.api.features.produtor.model.Produtor;
import com.defulo.api.features.usuario.model.Perfil;

@Component
public class ProdutorMapper {

    public Produtor toEntity(ProdutorCreateRequestDTO dto) {
        if (dto == null) return null;

        Produtor entity = new Produtor();
        entity.setNome(dto.nome());
        entity.setEmail(dto.email());
        entity.setSenha(dto.senha());
        entity.setCpf(dto.cpf());
        entity.setTelefone(dto.telefone());
        entity.setPropriedade(dto.propriedade());
        entity.setAreaTotal(dto.areaTotal());
        entity.setPerfil(Perfil.PRODUTOR);
        
        return entity;
    }

    public ProdutorResponseDTO toResponseDTO(Produtor entity) {
        if (entity == null) return null;

        return new ProdutorResponseDTO(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getCpf(),
                entity.getTelefone(),
                entity.getPropriedade(),
                entity.getAreaTotal(),
                entity.getDataCriacao(),
                entity.getDataAtualizacao()
        );
    }
}
