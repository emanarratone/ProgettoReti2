package com.uniupo.biglietto.controller;

import com.uniupo.biglietto.model.Biglietto;
import com.uniupo.biglietto.service.BigliettoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tickets")
public class BigliettoController {

    private final BigliettoService service;

    public BigliettoController(BigliettoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Biglietto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/targa/{targa}")
    public ResponseEntity<List<Biglietto>> getByTarga(@PathVariable String targa) {
        return ResponseEntity.ok(service.getByTarga(targa));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Biglietto biglietto) {
        try {
            Biglietto saved = service.create(biglietto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione biglietto"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore cancellazione biglietto"));
        }
    }
}
