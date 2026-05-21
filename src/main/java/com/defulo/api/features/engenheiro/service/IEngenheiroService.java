package com.defulo.api.features.engenheiro.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.defulo.api.features.engenheiro.dto.request.EngenheiroCreateRequestDTO;
import com.defulo.api.features.engenheiro.dto.request.EngenheiroUpdateRequestDTO;
import com.defulo.api.features.engenheiro.dto.response.EngenheiroResponseDTO;

public interface IEngenheiroService {

    EngenheiroResponseDTO salvar(EngenheiroCreateRequestDTO dto);

    EngenheiroResponseDTO buscarPorId(Long id);

    Page<EngenheiroResponseDTO> buscarTodos(Pageable pageable);

    EngenheiroResponseDTO atualizar(Long id, EngenheiroUpdateRequestDTO dto);

    void excluirPorId(Long id);
}