package com.uniupo.corsia.repository;

import com.uniupo.corsia.model.Corsia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorsiaRepository extends JpaRepository<Corsia, Integer> {

    List<Corsia> findCorsiaByCaselloOrderByCaselloAsc(Integer idCasello);

    List<Corsia> findByCaselloOrderByNumCorsiaAsc(Integer casello);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(MAX(c.numCorsia),0) FROM Corsia c WHERE c.casello = :casello")
    Integer findMaxNumCorsiaByCasello(@org.springframework.data.repository.query.Param("casello") Integer casello);

    void deleteByCaselloAndNumCorsia(Integer casello, Integer numCorsia);
}
