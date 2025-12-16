package com.uniupo.casello.controller;

import com.uniupo.casello.model.dto.CaselloDTO;
import com.uniupo.casello.service.CaselloService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/toll")
public class CaselloController {

    private final CaselloService service;

    public CaselloController(CaselloService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CaselloDTO>> getToll() {
        List<CaselloDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CaselloDTO>> searchToll(@RequestParam("q") String q) {
        List<CaselloDTO> list = service.search(q);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<?> createToll(@RequestBody CaselloDTO body) {
        try {
            if (body.getSigla() == null || body.getIdAutostrada() == null || body.getLimite() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "parametri obbligatori"));
            }
            CaselloDTO created = service.create(body);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione casello"));
        }
    }

    @PutMapping("/{idCasello}")
    public ResponseEntity<?> updateToll(@PathVariable Integer idCasello, @RequestBody CaselloDTO body) {
        try {
            if (body.getSigla() == null || body.getIdAutostrada() == null || body.getLimite() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "parametri obbligatori"));
            }
            CaselloDTO updated = service.update(idCasello, body);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore aggiornamento casello"));
        }
    }

    @DeleteMapping("/{idCasello}")
    public ResponseEntity<?> deleteToll(@PathVariable Integer idCasello) {
        try {
            service.delete(idCasello);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore cancellazione casello"));
        }
    }
}
