package com.uniupo.veicolo.controller;

import com.uniupo.veicolo.model.Veicolo;
import com.uniupo.veicolo.service.VeicoloService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vehicles")
public class VeicoloController {

    private final VeicoloService service;

    public VeicoloController(VeicoloService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Veicolo>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{targa}")
    public ResponseEntity<?> getByTarga(@PathVariable String targa) {
        return service.getByTarga(targa)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Veicolo>> getByTipo(@PathVariable Veicolo.TipoVeicolo tipo) {
        return ResponseEntity.ok(service.getByTipo(tipo));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Veicolo veicolo) {
        try {
            Veicolo saved = service.create(veicolo);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione veicolo"));
        }
    }

    @PutMapping("/{targa}")
    public ResponseEntity<?> update(@PathVariable String targa, @RequestBody Veicolo veicolo) {
        try {
            return service.update(targa, veicolo)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore aggiornamento veicolo"));
        }
    }

    @DeleteMapping("/{targa}")
    public ResponseEntity<?> delete(@PathVariable String targa) {
        try {
            service.delete(targa);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore cancellazione veicolo"));
        }
    }
}
