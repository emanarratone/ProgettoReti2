package com.uniupo.autostrada.repository;

import com.uniupo.autostrada.model.Autostrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutostradaRepository extends JpaRepository<Autostrada, Integer> {

    List<Autostrada> findAutostradasBySiglaOrderBySiglaAsc(String query);
}
