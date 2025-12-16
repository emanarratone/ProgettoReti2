package com.uniupo.casello.repository;

import com.uniupo.casello.model.Casello;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaselloRepository extends JpaRepository<Casello, Integer> {

    List<Casello> findCaselloBySiglaOrderBySiglaAsc(String query);

    List<Casello> findByIdAutostradaOrderBySiglaAsc(Integer idAutostrada);
}
