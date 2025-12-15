package com.example.regione.repository;

import com.example.regione.model.Regione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegioneRepository extends JpaRepository<Regione, Integer> {

    List<Regione> findTop20ByNomeContainingIgnoreCaseOrderByNomeAsc(String query);
}
