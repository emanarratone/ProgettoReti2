package com.uniupo.biglietto.repository;

import com.uniupo.biglietto.model.Biglietto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BigliettoRepository extends JpaRepository<Biglietto, Integer> {
    List<Biglietto> findByTarga(String targa);
}
