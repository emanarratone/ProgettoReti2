package com.example.utente.repository;

import com.example.utente.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, String> {
    List<Utente> findByIsAdmin(Boolean isAdmin);
}
