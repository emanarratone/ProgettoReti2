package corsia_service.repository;

import corsia_service.model.Corsia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface corsiaRepository extends JpaRepository<Corsia, Integer> {

    List<Corsia> findCorsiaByCaselloOrderByNumCorsiaAsc(String query);

}
