package autostrada_service.controller;


import autostrada_service.model.DTO.autostradaCreateUpdateDTO;
import autostrada_service.model.DTO.autostradaDTO;
import autostrada_service.service.autostradaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/highways")
public class autostradaController {

    private final autostradaService service;

    public autostradaController(autostradaService service){
        this.service = service;
    }

    // GET /highways
    @GetMapping
    public ResponseEntity<List<autostradaDTO>> gethighways() {
        List<autostradaDTO> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    // GET /highways/search?q=...
    @GetMapping("/search")
    public ResponseEntity<List<autostradaDTO>> searchhighways(@RequestParam("q") String q) {
        List<autostradaDTO> list = service.search(q);
        return ResponseEntity.ok(list);
    }

    // POST /highways
    @PostMapping
    public ResponseEntity<?> createRegion(@RequestBody autostradaCreateUpdateDTO body) {
        try {
            if (body.getSigla() == null || body.getSigla().isBlank() || body.getIdRegione() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "parametri obbligatori"));
            }
            autostradaDTO created = service.create(body);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione autostrada"));
        }
    }

    // PUT /highways/{idAutostrada}
    @PutMapping("/{idAutostrada}")
    public ResponseEntity<?> updateRegion(@PathVariable Integer idAutostrada,
                                          @RequestBody autostradaCreateUpdateDTO body) {
        try {
            if (body.getSigla() == null || body.getSigla().isBlank() || body.getIdRegione() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "nome obbligatorio"));
            }
            autostradaDTO updated = service.update(idAutostrada, body);
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

    // DELETE /regions/{idAutostrada}
    @DeleteMapping("/{idAutostrada}")
    public ResponseEntity<?> deleteRegion(@PathVariable Integer idAutostrada) {
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
