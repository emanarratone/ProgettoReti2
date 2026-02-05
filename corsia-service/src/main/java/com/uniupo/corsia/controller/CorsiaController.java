package com.uniupo.corsia.controller;

import com.uniupo.corsia.model.Corsia;
import com.uniupo.corsia.model.dto.CorsiaDTO;
import com.uniupo.corsia.service.CorsiaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CorsiaController {

    private final CorsiaService service;

    public CorsiaController(CorsiaService service) {
        this.service = service;
    }

    @GetMapping("/lanes")
    public ResponseEntity<List<CorsiaDTO>> getLanes() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/lanes/search")
    public ResponseEntity<?> searchLanes(@RequestParam("q") String q) {
        try {
            Integer idCasello = Integer.parseInt(q);
            return ResponseEntity.ok(service.search(idCasello));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "q must be casello id (integer)"));
        }
    }

    @PostMapping("/lanes")
    public ResponseEntity<?> createLane(@RequestBody CorsiaDTO body) {
        try {
            if (body.getCasello() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "casello obbligatorio"));
            }
            CorsiaDTO created = service.create(body);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione corsia"));
        }
    }

    @PutMapping("/lanes/{idCasello}/{numCorsia}")
    public ResponseEntity<?> updateLane(@PathVariable Integer idCasello,
                                        @PathVariable Integer numCorsia,
                                        @RequestBody Map<String, Object> body) {
        try {
            // Leggi i campi dal JSON come li manda il front-end
            String versoStr = (String) body.getOrDefault("verso", body.get("corsiaVerso"));
            String tipoStr  = (String) body.getOrDefault("tipo_corsia", body.get("corsiaTipo"));
            Object chiusoObj = body.getOrDefault("chiuso", body.getOrDefault("corsiaChiuso", false));

            if (versoStr == null || tipoStr == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "parametri obbligatori: verso, tipo_corsia"));
            }

            Corsia.Verso verso;
            try {
                verso = Corsia.Verso.valueOf(versoStr);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", "verso non valido"));
            }

            Corsia.Tipo tipo;
            try {
                tipo = Corsia.Tipo.valueOf(tipoStr);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", "tipo_corsia non valido"));
            }

            Boolean chiuso;
            if (chiusoObj instanceof Boolean b) chiuso = b;
            else if (chiusoObj instanceof String s) chiuso = Boolean.parseBoolean(s);
            else chiuso = false;

            // Costruisci un DTO minimale da passare al service
            CorsiaDTO dto = new CorsiaDTO(idCasello, numCorsia, verso, tipo, chiuso);

            CorsiaDTO updated = service.update(idCasello, numCorsia, dto);
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


    @DeleteMapping("/lanes/{idCasello}/{numCorsia}")
    public ResponseEntity<?> deleteLane(@PathVariable Integer idCasello,
                                        @PathVariable Integer numCorsia) {
        try {
            service.deleteByCaselloAndNum(idCasello, numCorsia);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore cancellazione corsia"));
        }
    }

    // -------- endpoint usati da gateway: /tolls/{idCasello}/lanes --------

    @GetMapping("/tolls/{idCasello}/lanes")
    public ResponseEntity<?> getLanesForToll(@PathVariable Integer idCasello) {
        return ResponseEntity.ok(service.search(idCasello));
    }

    @PostMapping("/tolls/{idCasello}/lanes")
    public ResponseEntity<?> createLaneForToll(@PathVariable Integer idCasello,
                                               @RequestBody Map<String,Object> body) {
        try {
            String verso = (String) body.getOrDefault("verso", body.get("corsiaVerso"));
            String tipo = (String) body.getOrDefault("tipo_corsia", body.get("corsiaTipo"));
            Boolean chiuso = (Boolean) body.getOrDefault("chiuso", body.getOrDefault("corsiaChiuso", false));
            var created = service.createForToll(idCasello, verso, tipo, chiuso);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Errore creazione corsia"));
        }
    }

}