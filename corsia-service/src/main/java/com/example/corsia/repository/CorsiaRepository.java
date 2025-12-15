package com.example.corsia.repository;

import com.example.corsia.model.Corsia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorsiaRepository extends JpaRepository<Corsia, Integer> {

    List<Corsia> findCorsiaByCaselloOrderByNumCorsiaAsc(String query);
}
