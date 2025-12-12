package regione_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import regione_service.model.Regione;

import java.util.List;

@Repository
public interface regioneRepository extends JpaRepository<Regione, Integer> {

    // per la ricerca autocomplete
    List<Regione> findTop20ByNomeContainingIgnoreCaseOrderByNomeAsc(String query);
}
