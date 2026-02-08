package com.uniupo.casello.controller;

import com.uniupo.casello.model.dto.CaselloDTO;
import com.uniupo.casello.service.CaselloService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CaselloController {

    private final CaselloService service;

    public CaselloController(CaselloService service) {
        this.service = service;
    }

    // ---------------- GLOBAL /tolls (optional) ----------------

    @GetMapping("/tolls")
    public ResponseEntity<List<CaselloDTO>> getTolls() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/tolls/search")
    public ResponseEntity<List<CaselloDTO>> searchTolls(@RequestParam("q") String q) {
        return ResponseEntity.ok(service.search(q));
    }

    @PostMapping("/tolls")
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

    @GetMapping("/highways/{idAutostrada}/tolls")
    public ResponseEntity<?> getTollsForHighway(@PathVariable Integer idAutostrada) {
        try {
            List<CaselloDTO> list = service.getByAutostrada(idAutostrada);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore interno casello-service"));
        }
    }

    @PostMapping("/highways/{idAutostrada}/tolls")
    public ResponseEntity<?> createTollForHighway(@PathVariable Integer idAutostrada,
                                                  @RequestBody Map<String, Object> body) {
        try {
            String nomeCasello = (String) body.getOrDefault("nome_casello", body.get("sigla"));
            Integer limite = body.get("limite") != null ? ((Number) body.get("limite")).intValue() : null;
            Boolean chiuso = (Boolean) body.getOrDefault("chiuso", body.getOrDefault("closed", false));

            if (nomeCasello == null || nomeCasello.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "nome_casello obbligatorio"));
            }

            CaselloDTO created = service.createForHighway(idAutostrada, nomeCasello, limite, chiuso);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione casello"));
        }
    }

    @PutMapping("/tolls/{idCasello}")
    public ResponseEntity<?> updateToll(@PathVariable Integer idCasello,
                                        @RequestBody Map<String, Object> body) {
        try {
            String sigla = (String) body.getOrDefault("sigla", body.get("nome_casello"));
            Integer limite = body.get("limite") != null ? ((Number) body.get("limite")).intValue() : null;
            Boolean chiuso = (Boolean) body.getOrDefault("chiuso", body.getOrDefault("closed", false));

            if (sigla == null || sigla.isBlank() || limite == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "parametri obbligatori"));
            }

            CaselloDTO existing = service.getById(idCasello)
                    .orElseThrow(() -> new IllegalArgumentException("Casello non trovato"));

            CaselloDTO dto = new CaselloDTO(
                    existing.getIdCasello(),
                    sigla,
                    existing.getIdAutostrada(),
                    chiuso,
                    limite
            );

            CaselloDTO updated = service.updateFromDTO(idCasello, dto);
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

    @DeleteMapping("/tolls/{idCasello}")
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
