package com.defulo.api.features.produtor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.defulo.api.features.produtor.model.Produtor;

@Repository
public interface ProdutorRepository extends JpaRepository<Produtor, Long> {
}
