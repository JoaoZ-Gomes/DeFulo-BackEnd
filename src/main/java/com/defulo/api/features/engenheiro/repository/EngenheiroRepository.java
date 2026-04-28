package com.defulo.api.features.engenheiro.repository;

import com.defulo.api.features.engenheiro.model.Engenheiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EngenheiroRepository extends JpaRepository<Engenheiro, Long> {

    Optional<Engenheiro> findByEmail(String email);

}