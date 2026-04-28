package com.defulo.api.features.usuario.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.defulo.api.features.usuario.dto.request.UsuarioCreateRequestDTO;
import com.defulo.api.features.usuario.dto.request.UsuarioUpdateRequestDTO;
import com.defulo.api.features.usuario.dto.response.UsuarioResponseDTO;

/**
 * Interface de serviço para Usuario.
 * Define o contrato para operações de negócio seguindo os padrões REST.
 */
public interface UsuarioIService {

    /**
     * Salva um novo usuário no sistema.
     * @param dto dados de criação
     * @return DTO de resposta
     */
    UsuarioResponseDTO salvar(UsuarioCreateRequestDTO dto);

    /**
     * Busca um usuário pelo ID único.
     * @param id identificador
     * @return DTO de resposta
     */
    UsuarioResponseDTO buscarPorId(Long id);

    /**
     * Lista todos os usuários de forma paginada.
     * @param pageable parâmetros de paginação
     * @return Página de DTOs
     */
    Page<UsuarioResponseDTO> buscarTodos(Pageable pageable);

    /**
     * Busca um usuário pelo email.
     * @param email endereço de email
     * @return DTO de resposta
     */
    UsuarioResponseDTO buscarPorEmail(String email);

    /**
     * Atualiza dados de um usuário existente.
     * @param id identificador
     * @param dto dados para atualização
     * @return DTO de resposta atualizado
     */
    UsuarioResponseDTO atualizar(Long id, UsuarioUpdateRequestDTO dto);

    /**
     * Remove um usuário do sistema.
     * @param id identificador
     */
    void excluirPorId(Long id);
}