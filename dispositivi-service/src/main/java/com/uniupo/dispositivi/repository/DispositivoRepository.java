package com.uniupo.dispositivi.repository;

import com.uniupo.dispositivi.model.Dispositivo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispositivoRepository extends JpaRepository<Dispositivo, Integer> {
    List<Dispositivo> findByCasello(Integer casello);
    List<Dispositivo> findByCorsia(Integer corsia);
    List<Dispositivo> findByStatus(Boolean status);
    List<Dispositivo> findByCaselloAndCorsia(Integer casello, Integer corsia);
    @Transactional
    void deleteByCaselloAndCorsia(Integer casello, Integer corsia);
}
