package com.defulo.api.features.rtv.mapper;

import org.springframework.stereotype.Component;

import com.defulo.api.features.rtv.dto.request.RtvCreateRequestDTO;
import com.defulo.api.features.rtv.dto.response.RtvResponseDTO;
import com.defulo.api.features.rtv.model.Rtv;
import com.defulo.api.features.usuario.model.Perfil;

@Component
public class RtvMapper {

    public Rtv toEntity(RtvCreateRequestDTO dto) {
        if (dto == null) return null;

        Rtv rtv = new Rtv();
        rtv.setNome(dto.nome());
        rtv.setEmail(dto.email());
        rtv.setSenha(dto.senha());
        rtv.setCpf(dto.cpf());
        rtv.setTelefone(dto.telefone());
        rtv.setRegiao(dto.regiao());
        rtv.setCodigoRtv(dto.codigoRtv());
        rtv.setPerfil(Perfil.RTV);
        
        return rtv;
    }

    public RtvResponseDTO toResponseDTO(Rtv entity) {
        if (entity == null) return null;

        return new RtvResponseDTO(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getCpf(),
                entity.getTelefone(),
                entity.getRegiao(),
                entity.getCodigoRtv(),
                entity.getDataCriacao(),
                entity.getDataAtualizacao()
        );
    }
}
