package com.uniupo.biglietto.repository;

import com.uniupo.biglietto.model.Biglietto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BigliettoRepository extends JpaRepository<Biglietto, Integer> {
    List<Biglietto> findByTarga(String targa);
    Optional<Biglietto> findFirstByTargaOrderByIdBigliettoDesc(String targa);
    @Query(value = """
    SELECT 
      COUNT(*)::int,
      GREATEST(1, COUNT(DISTINCT DATE(timestamp_in)))::int
    FROM biglietto
    """, nativeQuery = true)
    List<Object[]> getTrafficStatsRaw();


}
