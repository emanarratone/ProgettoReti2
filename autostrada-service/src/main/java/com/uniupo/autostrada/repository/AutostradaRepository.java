package com.uniupo.autostrada.repository;

import com.uniupo.autostrada.model.Autostrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutostradaRepository extends JpaRepository<Autostrada, Integer> {

    List<Autostrada> findAutostradasBySiglaOrderBySiglaAsc(String query);

    List<Autostrada> findByIdRegioneOrderBySiglaAsc(Integer idRegione);

    @Query(value = "SELECT * FROM autostrada WHERE id_autostrada IN (" +
            "SELECT MIN(id_autostrada) FROM autostrada GROUP BY sigla) " +
            "LIMIT 5", nativeQuery = true)
    List<Autostrada> findTop5Unique();
}
