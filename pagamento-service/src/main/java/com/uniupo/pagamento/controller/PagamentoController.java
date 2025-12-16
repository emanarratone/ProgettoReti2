package com.uniupo.pagamento.controller;

import com.uniupo.pagamento.model.Pagamento;
import com.uniupo.pagamento.service.PagamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Pagamento>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/biglietto/{idBiglietto}")
    public ResponseEntity<List<Pagamento>> getByBiglietto(@PathVariable Integer idBiglietto) {
        return ResponseEntity.ok(service.getByBiglietto(idBiglietto));
    }

    @GetMapping("/unpaid")
    public ResponseEntity<List<Pagamento>> getUnpaid() {
        return ResponseEntity.ok(service.getUnpaid());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Pagamento pagamento) {
        try {
            Pagamento saved = service.create(pagamento);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione pagamento"));
        }
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<?> markAsPaid(@PathVariable Integer id) {
        try {
            return service.markAsPaid(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore aggiornamento pagamento"));
        }
    }
}
