package regione_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import regione_service.model.Regione;
import regione_service.repository.regioneRepository;

import java.util.Map;

@RestController
public class regioneController {

    // ---- REGIONI ----
    @GetMapping("/regions")
    public ResponseEntity<String> getRegions() {
        try {
            regioneRepository dao = new regioneRepository();
            String json = dao.getregioneJson();
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // POST /api/regions { "nomeRegione": "Lombardia" }
    @PostMapping("/regions")
    public ResponseEntity<String> createRegion(@RequestBody Map<String, Object> body) {
        try {
            String nome = (String) body.get("nomeRegione");
            if (nome == null || nome.isBlank()) {
                return ResponseEntity.badRequest()
                        .body("{\"error\":\"nomeRegione obbligatorio\"}");
            }
            Regione r = new Regione(nome);
            regioneRepository dao = new regioneRepository();
            dao.insertRegione(r);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("{\"status\":\"ok\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Errore creazione regione\"}");
        }
    }

    // PUT /api/regions/{idRegione}
    @PutMapping("/regions/{idRegione}")
    public ResponseEntity<String> updateRegion(@PathVariable int idRegione,
                                               @RequestBody Map<String, Object> body) {
        try {
            String nome = (String) body.get("nomeRegione");
            if (nome == null || nome.isBlank()) {
                return ResponseEntity.badRequest()
                        .body("{\"error\":\"nomeRegione obbligatorio\"}");
            }
            Regione r = new Regione(nome);
            regioneRepository dao = new regioneRepository();
            dao.updateRegione(idRegione, r);
            return ResponseEntity.ok("{\"status\":\"ok\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Errore aggiornamento regione\"}");
        }
    }

    // DELETE /api/regions/{idRegione}
    @DeleteMapping("/regions/{idRegione}")
    public ResponseEntity<String> deleteRegion(@PathVariable int idRegione) {
        try {
            regioneRepository dao = new regioneRepository();
            dao.deleteRegione(idRegione);
            return ResponseEntity.ok("{\"status\":\"ok\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Errore cancellazione regione\"}");
        }
    }
}
