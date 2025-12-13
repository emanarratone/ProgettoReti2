package autostrada_service.repository;

import autostrada_service.model.Autostrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface autostradaRepository extends JpaRepository<Autostrada, Integer> {

    List<Autostrada> findAutostradasBySiglaOrderBySiglaAsc(String query);

}
