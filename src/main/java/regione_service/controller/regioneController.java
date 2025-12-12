package regione_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import regione_service.model.DTO.regioneCreateUpdateDto;
import regione_service.model.DTO.regioneDTO;
import regione_service.service.regioneService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/regions")
public class regioneController {

    private final regioneService service;

    public regioneController(regioneService service) {
        this.service = service;
    }

    // GET /regions
    @GetMapping
    public ResponseEntity<List<regioneDTO>> getRegions() {
        List<regioneDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    // GET /regions/search?q=...
    @GetMapping("/search")
    public ResponseEntity<List<regioneDTO>> searchRegions(@RequestParam("q") String q) {
        List<regioneDTO> list = service.search(q);
        return ResponseEntity.ok(list);
    }

    // POST /regions { "nome": "Lombardia" }
    @PostMapping
    public ResponseEntity<?> createRegion(@RequestBody regioneCreateUpdateDto body) {
        try {
            if (body.getNome() == null || body.getNome().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "nome obbligatorio"));
            }
            regioneDTO created = service.create(body);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione regione"));
        }
    }

    // PUT /regions/{idRegione}
    @PutMapping("/{idRegione}")
    public ResponseEntity<?> updateRegion(@PathVariable Integer idRegione,
                                          @RequestBody regioneCreateUpdateDto body) {
        try {
            if (body.getNome() == null || body.getNome().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "nome obbligatorio"));
            }
            regioneDTO updated = service.update(idRegione, body);
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

    // DELETE /regions/{idRegione}
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
