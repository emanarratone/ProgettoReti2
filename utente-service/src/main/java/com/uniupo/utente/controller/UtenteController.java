package com.uniupo.utente.controller;

import com.uniupo.utente.model.Utente;
import com.uniupo.utente.service.UtenteService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "https://localhost:8080")
public class UtenteController {

    private final UtenteService service;
    private static final Logger logger = LoggerFactory.getLogger(UtenteController.class);

    public UtenteController(UtenteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Utente>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getByUsername(@PathVariable String username) {
        return service.getByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/admins")
    public ResponseEntity<List<Utente>> getAdmins() {
        return ResponseEntity.ok(service.getAdmins());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Utente utente) {
        try {
            Utente saved = service.create(utente);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione utente"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        return service.login(username, password)
                .map(u -> {
                    // FONDAMENTALE: Salva l'utente nella sessione qui!
                    session.setAttribute("user", u);
                    logger.info("Sessione creata per l'utente: {}", username);

                    return ResponseEntity.ok(Map.of("success", true, "user", u));
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "error", "Credenziali non valide")));
    }

    @PutMapping("/{username}/password")
    public ResponseEntity<?> updatePassword(@PathVariable String username,
                                            @RequestBody Map<String, String> passwords) {
        try {
            String oldPassword = passwords.get("oldPassword");
            String newPassword = passwords.get("newPassword");

            return service.updatePassword(username, oldPassword, newPassword)
                    .map(u -> ResponseEntity.ok(Map.of("success", true, "message", "Password aggiornata")))
                    .orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("success", false, "error", "Password corrente non valida")));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore aggiornamento password"));
        }
    }

    @PutMapping("/{username}/toggle-admin")
    public ResponseEntity<?> toggleAdmin(@PathVariable String username) {
        try {
            return service.toggleAdmin(username)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore modifica ruolo"));
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> delete(@PathVariable String username) {
        try {
            service.delete(username);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore cancellazione utente"));
        }
    }
}
