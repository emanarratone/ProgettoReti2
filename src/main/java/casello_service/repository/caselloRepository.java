package casello_service.repository;

import casello_service.model.Casello;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface caselloRepository extends JpaRepository<Casello, Integer> {

    List<Casello> findCaselloBySiglaOrderBySiglaAsc(String query);

}
