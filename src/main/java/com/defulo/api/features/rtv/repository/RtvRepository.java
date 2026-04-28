package com.defulo.api.features.rtv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.defulo.api.features.rtv.model.Rtv;

@Repository
public interface RtvRepository extends JpaRepository<Rtv, Long> {
}
