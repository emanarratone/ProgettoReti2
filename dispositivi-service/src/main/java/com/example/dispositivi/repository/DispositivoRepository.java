package com.example.dispositivi.repository;

import com.example.dispositivi.model.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispositivoRepository extends JpaRepository<Dispositivo, Integer> {
    List<Dispositivo> findByCasello(Integer casello);
    List<Dispositivo> findByCorsia(Integer corsia);
    List<Dispositivo> findByStatus(Boolean status);
}
