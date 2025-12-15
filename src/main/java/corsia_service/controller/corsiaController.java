package corsia_service.controller;

import corsia_service.model.corsiaDTO;
import corsia_service.service.corsiaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lane")
public class corsiaController {

    private final corsiaService service;

    public corsiaController(corsiaService service) {
        this.service = service;
    }

    // GET /lane
    @GetMapping
    public ResponseEntity<List<corsiaDTO>> getlane() {
        List<corsiaDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    // GET /lane/search?q=...
    @GetMapping("/search")
    public ResponseEntity<List<corsiaDTO>> searchlane(@RequestParam("q") String q) {
        List<corsiaDTO> list = service.search(q);
        return ResponseEntity.ok(list);
    }

    // POST /toll
    @PostMapping
    public ResponseEntity<?> createlane(@RequestBody corsiaDTO body) {
        try {
            if (body.getCasello() == null || body.getNumCorsia() == null || body.getVerso() == null || body.getTipo() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "\"parametri obbligatori\""));
            }
            corsiaDTO created = service.create(body);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione casello"));
        }
    }

    // PUT /lane/{idCasello}
    @PutMapping("/{idCasello}-")
    public ResponseEntity<?> updatelane(@PathVariable Integer idCasello,
                                        @RequestBody corsiaDTO body) {
        try {
            if (body.getCasello() == null || body.getNumCorsia() == null || body.getVerso() == null || body.getTipo() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "nome obbligatorio"));
            }
            corsiaDTO updated = service.update(idCasello, body.getNumCorsia(), body);
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

    // DELETE /lane/{idCasello}
    @DeleteMapping("/{idCasello}")
    public ResponseEntity<?> deletelane(@PathVariable Integer idCasello) {
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
