package com.example.casello.repository;

import com.example.casello.model.Casello;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaselloRepository extends JpaRepository<Casello, Integer> {

    List<Casello> findCaselloBySiglaOrderBySiglaAsc(String query);
}
