package com.uniupo.multa.controller;

import com.uniupo.multa.model.Multa;
import com.uniupo.multa.service.MultaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fines")
public class MultaController {

    private final MultaService service;

    public MultaController(MultaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Multa>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/targa/{targa}")
    public ResponseEntity<List<Multa>> getByTarga(@PathVariable String targa) {
        return ResponseEntity.ok(service.getByTarga(targa));
    }

    @GetMapping("/targa/{targa}/total")
    public ResponseEntity<?> getTotalUnpaidByTarga(@PathVariable String targa) {
        Double total = service.getTotalUnpaidByTarga(targa);
        return ResponseEntity.ok(Map.of("targa", targa, "totalUnpaid", total));
    }

    @GetMapping("/unpaid")
    public ResponseEntity<List<Multa>> getUnpaid() {
        return ResponseEntity.ok(service.getUnpaid());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Multa multa) {
        try {
            Multa saved = service.create(multa);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione multa"));
        }
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<?> markAsPaid(@PathVariable Integer id) {
        try {
            return service.markAsPaid(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore aggiornamento multa"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore cancellazione multa"));
        }
    }
}
