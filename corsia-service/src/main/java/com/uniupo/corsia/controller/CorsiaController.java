package com.uniupo.corsia.controller;

import com.uniupo.corsia.model.dto.CorsiaDTO;
import com.uniupo.corsia.service.CorsiaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lane")
public class CorsiaController {

    private final CorsiaService service;

    public CorsiaController(CorsiaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CorsiaDTO>> getLane() {
        List<CorsiaDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CorsiaDTO>> searchLane(@RequestParam("q") String q) {
        List<CorsiaDTO> list = service.search(Integer.getInteger(q));
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<?> createLane(@RequestBody CorsiaDTO body) {
        try {
            if (body.getCasello() == null || body.getNumCorsia() == null || body.getVerso() == null || body.getTipo() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "parametri obbligatori"));
            }
            CorsiaDTO created = service.create(body);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione corsia"));
        }
    }

    @PutMapping("/{idCasello}")
    public ResponseEntity<?> updateLane(@PathVariable Integer idCasello, @RequestBody CorsiaDTO body) {
        try {
            if (body.getCasello() == null || body.getNumCorsia() == null || body.getVerso() == null || body.getTipo() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "parametri obbligatori"));
            }
            CorsiaDTO updated = service.update(idCasello, body.getNumCorsia(), body);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore aggiornamento corsia"));
        }
    }

    @DeleteMapping("/{idCasello}")
    public ResponseEntity<?> deleteLane(@PathVariable Integer idCasello) {
        try {
            service.delete(idCasello);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore cancellazione corsia"));
        }
    }
}
