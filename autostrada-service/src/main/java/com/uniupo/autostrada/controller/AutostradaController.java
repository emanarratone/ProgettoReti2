package com.uniupo.autostrada.controller;

import com.uniupo.autostrada.model.dto.AutostradaCreateUpdateDTO;
import com.uniupo.autostrada.model.dto.AutostradaDTO;
import com.uniupo.autostrada.service.AutostradaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/highways")
public class AutostradaController {

    private final AutostradaService service;

    public AutostradaController(AutostradaService service){
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AutostradaDTO>> getHighways() {
        List<AutostradaDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AutostradaDTO>> searchHighways(@RequestParam("q") String q) {
        List<AutostradaDTO> list = service.search(q);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<?> createHighway(@RequestBody AutostradaCreateUpdateDTO body) {
        try {
            if (body.getSigla() == null || body.getSigla().isBlank() || body.getIdRegione() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "parametri obbligatori"));
            }
            AutostradaDTO created = service.create(body);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione autostrada"));
        }
    }

    @PutMapping("/{idAutostrada}")
    public ResponseEntity<?> updateHighway(@PathVariable Integer idAutostrada,
                                          @RequestBody AutostradaCreateUpdateDTO body) {
        try {
            if (body.getSigla() == null || body.getSigla().isBlank() || body.getIdRegione() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "parametri obbligatori"));
            }
            AutostradaDTO updated = service.update(idAutostrada, body);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore aggiornamento autostrada"));
        }
    }

    @DeleteMapping("/{idAutostrada}")
    public ResponseEntity<?> deleteHighway(@PathVariable Integer idAutostrada) {
        try {
            service.delete(idAutostrada);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore cancellazione autostrada"));
        }
    }
}
