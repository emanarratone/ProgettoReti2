package com.example.multa.repository;

import com.example.multa.model.Multa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultaRepository extends JpaRepository<Multa, Integer> {
    List<Multa> findByTarga(String targa);
    List<Multa> findByPagato(Boolean pagato);
}
