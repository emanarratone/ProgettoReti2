package auto_service.repository;

import auto_service.model.Auto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface autoRepository extends JpaRepository<Auto, Integer> {

    List<Auto> searchVehicles(Auto auto);
}
