package casello_service.controller;


import casello_service.model.DTO.caselloDTO;
import casello_service.service.caselloService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/toll")
public class caselloController {

    private final caselloService service;

    public caselloController(caselloService service) {
        this.service = service;
    }

    // GET /toll
    @GetMapping
    public ResponseEntity<List<caselloDTO>> gettoll() {
        List<caselloDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    // GET /toll/search?q=...
    @GetMapping("/search")
    public ResponseEntity<List<caselloDTO>> searchtoll(@RequestParam("q") String q) {
        List<caselloDTO> list = service.search(q);
        return ResponseEntity.ok(list);
    }

    // POST /toll
    @PostMapping
    public ResponseEntity<?> createtoll(@RequestBody caselloDTO body) {
        try {
            if (body.getSigla() == null || body.getIdAutostrada() == null || body.getLimite() ==null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "\"parametri obbligatori\""));
            }
            caselloDTO created = service.create(body);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione casello"));
        }
    }

    // PUT /toll/{idCasello}
    @PutMapping("/{idCasello}")
    public ResponseEntity<?> updatetoll(@PathVariable Integer idCasello,
                                          @RequestBody caselloDTO body) {
        try {
            if (body.getSigla() == null || body.getIdAutostrada() == null || body.getLimite() ==null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "nome obbligatorio"));
            }
            caselloDTO updated = service.update(idCasello, body);
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

    // DELETE /toll/{idCasello}
    @DeleteMapping("/{idCasello}")
    public ResponseEntity<?> deleteRegion(@PathVariable Integer idCasello) {
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
