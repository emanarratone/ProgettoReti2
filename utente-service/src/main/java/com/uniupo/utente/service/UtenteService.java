package com.uniupo.utente.service;

import com.uniupo.utente.model.Utente;
import com.uniupo.utente.repository.UtenteRepository;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtenteService {

    private final UtenteRepository repository;

    public UtenteService(UtenteRepository repository) {
        this.repository = repository;
    }

    public List<Utente> getAll() {
        return repository.findAll();
    }

    public Optional<Utente> getByUsername(String username) {
        return repository.findById(username);
    }

    public List<Utente> getAdmins() {
        return repository.findByIsAdmin(true);
    }

    public List<Utente> getRegularUsers() {
        return repository.findByIsAdmin(false);
    }

    public boolean exists(String username) {
        return repository.existsById(username);
    }

    @Transactional
    public Utente create(Utente utente) {
        String hashedPassword = BCrypt.hashpw(utente.getPassword(), BCrypt.gensalt());
        utente.setPassword(hashedPassword);
        return repository.save(utente);
    }

    public boolean authenticate(String username, String password) {
        return repository.findById(username)
                .map(u -> BCrypt.checkpw(password, u.getPassword()))
                .orElse(false);
    }

    public Optional<Utente> login(String username, String password) {
        return repository.findById(username)
                .filter(u -> BCrypt.checkpw(password, u.getPassword()));
    }

    @Transactional
    public Optional<Utente> updatePassword(String username, String oldPassword, String newPassword) {
        return repository.findById(username)
                .filter(u -> BCrypt.checkpw(oldPassword, u.getPassword()))
                .map(u -> {
                    String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                    u.setPassword(hashedPassword);
                    return repository.save(u);
                });
    }

    @Transactional
    public Optional<Utente> toggleAdmin(String username) {
        return repository.findById(username)
                .map(u -> {
                    u.setIsAdmin(!u.getIsAdmin());
                    return repository.save(u);
                });
    }

    @Transactional
    public void delete(String username) {
        repository.deleteById(username);
    }
}
