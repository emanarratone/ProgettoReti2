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
    List<Object[]> getTraffiAverage30d();

    //dato gli ultimi 30gg so quanti biglietti sono stati generati quotidianamente
    @Query(value = """
            SELECT 
            DATE(timestamp_in) AS data,
            COUNT(*)::int AS biglietti_giornalieri
            FROM biglietto 
            WHERE timestamp_in >= NOW() - INTERVAL '30 days'
            GROUP BY DATE(timestamp_in)
            ORDER BY data DESC;""", nativeQuery = true)
    List<Object[]> getTraffic30days();

    @Query(value = """
            SELECT 
            EXTRACT(HOUR FROM timestamp_in)::int AS ora,
            COUNT(*) AS conteggio
            FROM biglietto 
            WHERE timestamp_in >= NOW() - INTERVAL '24 hours'
            GROUP BY ora
            ORDER BY ora;""", nativeQuery = true)
    List<Object[]> getTraffic24hours();
}
