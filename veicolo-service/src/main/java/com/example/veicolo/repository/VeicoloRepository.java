package com.example.veicolo.repository;

import com.example.veicolo.model.Veicolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VeicoloRepository extends JpaRepository<Veicolo, String> {
    List<Veicolo> findByTipoVeicolo(Veicolo.TipoVeicolo tipoVeicolo);
}
