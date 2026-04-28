package com.defulo.api.features.usuario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.defulo.api.features.usuario.model.Perfil;
import com.defulo.api.features.usuario.model.Usuario;
import java.util.List;

/**
 * Repositório para operações de banco de dados na entidade Usuario.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByCpf(String cpf);

    List<Usuario> findByPerfil(Perfil perfil);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
}