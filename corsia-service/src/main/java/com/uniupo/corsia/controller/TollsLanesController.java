package com.uniupo.corsia.controller;

import com.uniupo.corsia.service.CorsiaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tolls")
public class TollsLanesController {
    private final CorsiaService service;

    public TollsLanesController(CorsiaService service) {
        this.service = service;
    }

    @GetMapping("/{idCasello}/lanes")
    public ResponseEntity<?> getLanesForToll(@PathVariable Integer idCasello) {
        return ResponseEntity.ok(service.search(idCasello));
    }

    @PostMapping("/{idCasello}/lanes")
    public ResponseEntity<?> createLaneForToll(@PathVariable Integer idCasello, @RequestBody java.util.Map<String,Object> body) {
        try {
            String verso = (String) body.getOrDefault("verso", body.get("corsiaVerso"));
            String tipo = (String) body.getOrDefault("tipo_corsia", body.get("corsiaTipo"));
            Boolean chiuso = (Boolean) body.getOrDefault("chiuso", body.getOrDefault("corsiaChiuso", false));
            var created = service.createForToll(idCasello, verso, tipo, chiuso);
            return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(java.util.Map.of("error", "Errore creazione corsia"));
        }
    }

    @PutMapping("/lanes/{idCasello}/{numCorsia}")
    public ResponseEntity<?> updateLaneLegacy(@PathVariable Integer idCasello, @PathVariable Integer numCorsia, @RequestBody java.util.Map<String,Object> body) {
        try {
            String versoStr = (String) body.getOrDefault("verso", body.getOrDefault("corsiaVerso", "ENTRATA"));
            String tipoStr = (String) body.getOrDefault("tipo_corsia", body.getOrDefault("corsiaTipo", "NORMALE"));
            Boolean chiuso = (Boolean) body.getOrDefault("chiuso", body.getOrDefault("corsiaChiuso", false));

            com.uniupo.corsia.model.dto.CorsiaDTO dto = new com.uniupo.corsia.model.dto.CorsiaDTO(idCasello, numCorsia,
                    com.uniupo.corsia.model.Corsia.Verso.valueOf(versoStr), com.uniupo.corsia.model.Corsia.Tipo.valueOf(tipoStr), chiuso);

            var updated = service.update(idCasello, numCorsia, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(java.util.Map.of("error", "Errore aggiornamento corsia"));
        }
    }

    @DeleteMapping("/lanes/{idCasello}/{numCorsia}")
    public ResponseEntity<?> deleteLaneLegacy(@PathVariable Integer idCasello, @PathVariable Integer numCorsia) {
        try {
            service.deleteByCaselloAndNum(idCasello, numCorsia);
            return ResponseEntity.ok(java.util.Map.of("status", "ok"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(java.util.Map.of("error", "Errore cancellazione corsia"));
        }
    }
}
