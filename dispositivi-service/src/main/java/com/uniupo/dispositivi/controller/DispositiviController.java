package com.uniupo.dispositivi.controller;

import com.uniupo.dispositivi.model.Dispositivo;
import com.uniupo.dispositivi.service.DispositiviService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/devices")
public class DispositiviController {

    private final DispositiviService service;

    public DispositiviController(DispositiviService service) {
        this.service = service;
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "dispositivi-service"));
    }

    @GetMapping
    public ResponseEntity<List<Dispositivo>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sbarre")
    public ResponseEntity<?> getSbarre() {
        return ResponseEntity.ok(service.getSbarre());
    }

    @GetMapping("/telecamere")
    public ResponseEntity<?> getTelecamere() {
        return ResponseEntity.ok(service.getTelecamere());
    }

    @GetMapping("/totem")
    public ResponseEntity<?> getTotem() {
        return ResponseEntity.ok(service.getTotem());
    }

    @GetMapping("/casello/{casello}")
    public ResponseEntity<List<Dispositivo>> getByCasello(@PathVariable Integer casello) {
        return ResponseEntity.ok(service.getByCasello(casello));
    }

    @GetMapping("/corsia/{corsia}")
    public ResponseEntity<List<Dispositivo>> getByCorsia(@PathVariable Integer corsia) {
        return ResponseEntity.ok(service.getByCorsia(corsia));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Dispositivo>> getActive() {
        return ResponseEntity.ok(service.getActive());
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<Dispositivo>> getInactive() {
        return ResponseEntity.ok(service.getInactive());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Dispositivo dispositivo) {
        try {
            Dispositivo saved = service.create(dispositivo);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione dispositivo"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            // allow partial updates with 'stato' or 'status' keys or full dispositivo representation
            Boolean status;
            if (body.containsKey("stato")) {
                status = "ATTIVO".equalsIgnoreCase((String) body.get("stato")) || Boolean.TRUE.equals(body.get("stato"));
            } else if (body.containsKey("status")) {
                status = Boolean.TRUE.equals(body.get("status"));
            } else {
                status = null;
            }

            Integer casello = body.containsKey("casello") ? ((Number) body.get("casello")).intValue() : null;
            Integer corsia = body.containsKey("corsia") ? ((Number) body.get("corsia")).intValue() : null;

            return service.getById(id).map(existing -> {
                if (status != null) existing.setStatus(status);
                if (casello != null) existing.setCasello(casello);
                if (corsia != null) existing.setCorsia(corsia);
                Dispositivo saved = service.create(existing);
                return ResponseEntity.ok(saved);
            }).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Errore aggiornamento dispositivo"));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestBody Map<String, Boolean> body) {
        try {
            Boolean status = body.get("status");
            service.updateStatus(id, status);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore aggiornamento stato"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore cancellazione dispositivo"));
        }
    }
}
