package com.uniupo.veicolo.repository;

import com.uniupo.veicolo.model.Veicolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VeicoloRepository extends JpaRepository<Veicolo, String> {
    List<Veicolo> findByTipoVeicolo(Veicolo.TipoVeicolo tipoVeicolo);
}
