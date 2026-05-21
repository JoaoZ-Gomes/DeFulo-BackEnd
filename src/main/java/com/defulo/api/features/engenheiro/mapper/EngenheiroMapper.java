package com.defulo.api.features.engenheiro.mapper;

import com.defulo.api.features.engenheiro.dto.request.EngenheiroCreateRequestDTO;
import com.defulo.api.features.engenheiro.dto.request.EngenheiroUpdateRequestDTO;
import com.defulo.api.features.engenheiro.dto.response.EngenheiroResponseDTO;
import com.defulo.api.features.engenheiro.model.Engenheiro;
import com.defulo.api.features.usuario.model.Perfil;
import org.springframework.stereotype.Component;

/**
 * Mapper manual para Engenheiro.
 * Mantém consistência com o padrão @Component adotado no projeto (ProdutorMapper, RtvMapper).
 */
@Component
public class EngenheiroMapper {

    public Engenheiro toEntity(EngenheiroCreateRequestDTO dto) {
        if (dto == null) return null;

        Engenheiro engenheiro = new Engenheiro();
        engenheiro.setNome(dto.nome());
        engenheiro.setEmail(dto.email());
        engenheiro.setSenha(dto.senha()); // codificação feita no serviço
        engenheiro.setCpf(dto.cpf());
        engenheiro.setTelefone(dto.telefone());
        engenheiro.setEspecialidade(dto.especialidade());
        engenheiro.setPerfil(Perfil.ENGENHEIRO);

        return engenheiro;
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

    /**
     * Atualização parcial: aplica apenas campos não-nulos do DTO sobre a entidade.
     */
    public void updateEntityFromDTO(EngenheiroUpdateRequestDTO dto, Engenheiro entity) {
        if (dto == null) return;

        if (dto.nome() != null)         entity.setNome(dto.nome());
        if (dto.telefone() != null)     entity.setTelefone(dto.telefone());
        if (dto.especialidade() != null) entity.setEspecialidade(dto.especialidade());
    }
}