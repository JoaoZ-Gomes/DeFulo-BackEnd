package com.defulo.api.features.engenheiro.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.defulo.api.features.engenheiro.dto.request.EngenheiroCreateRequestDTO;
import com.defulo.api.features.engenheiro.dto.response.EngenheiroResponseDTO;

public interface IEngenheiroService {

    EngenheiroResponseDTO salvar(EngenheiroCreateRequestDTO dto);

    EngenheiroResponseDTO buscarPorId(Long id);

    Page<EngenheiroResponseDTO> buscarTodos(Pageable pageable);
    
    // Mantendo assinaturas legadas se necessário, mas focando no padrão profissional
}