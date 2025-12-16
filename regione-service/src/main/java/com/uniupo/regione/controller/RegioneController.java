package com.uniupo.regione.controller;

import com.uniupo.regione.model.dto.RegioneCreateUpdateDTO;
import com.uniupo.regione.model.dto.RegioneDTO;
import com.uniupo.regione.service.RegioneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/regions")
public class RegioneController {

    private final RegioneService service;

    public RegioneController(RegioneService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RegioneDTO>> getRegions() {
        List<RegioneDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RegioneDTO>> searchRegions(@RequestParam("q") String q) {
        List<RegioneDTO> list = service.search(q);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<?> createRegion(@RequestBody RegioneCreateUpdateDTO body) {
        try {
            if (body.getNome() == null || body.getNome().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "nome obbligatorio"));
            }
            RegioneDTO created = service.create(body);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione regione"));
        }
    }

    @PutMapping("/{idRegione}")
    public ResponseEntity<?> updateRegion(@PathVariable Integer idRegione,
                                          @RequestBody RegioneCreateUpdateDTO body) {
        try {
            if (body.getNome() == null || body.getNome().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "nome obbligatorio"));
            }
            RegioneDTO updated = service.update(idRegione, body);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore aggiornamento regione"));
        }
    }

    @DeleteMapping("/{idRegione}")
    public ResponseEntity<?> deleteRegion(@PathVariable Integer idRegione) {
        try {
            service.delete(idRegione);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore cancellazione regione"));
        }
    }
}
