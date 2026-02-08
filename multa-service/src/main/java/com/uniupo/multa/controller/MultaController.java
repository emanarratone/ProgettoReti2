package com.uniupo.multa.controller;

import com.uniupo.multa.model.Multa;
import com.uniupo.multa.model.dto.MultaDTO;
import com.uniupo.multa.model.dto.PagamentoDTO;
import com.uniupo.multa.service.MultaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/fines")
public class MultaController {

    private final MultaService service;
    private final WebClient webClient;

    public MultaController(MultaService service, WebClient webClient) {
        this.service = service;
        this.webClient = webClient;
    }

    @GetMapping
    public ResponseEntity<List<Multa>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/targa/{targa}")
    public ResponseEntity<List<Multa>> getByTarga(@PathVariable String targa) {
        return ResponseEntity.ok(service.getByTarga(targa));
    }

    @GetMapping("/targa/{targa}/total")
    public ResponseEntity<?> getTotalUnpaidByTarga(@PathVariable String targa) {
        Double total = service.getTotalUnpaidByTarga(targa);
        return ResponseEntity.ok(Map.of("targa", targa, "totalUnpaid", total));
    }

    @GetMapping("/unpaid")
    public ResponseEntity<List<Multa>> getUnpaid() {
        return ResponseEntity.ok(service.getUnpaid());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Multa multa) {
        try {
            Multa saved = service.create(multa);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione multa"));
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
                    .body(Map.of("error", "Errore aggiornamento multa"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore cancellazione multa"));
        }
    }

    @GetMapping("/list-joined")
    @CircuitBreaker(name = "paymentService", fallbackMethod = "fallbackPayments")
    public ResponseEntity<List<MultaDTO>> getJoinedFines() {
        // 1. Dati locali (DB Multe)
        List<Multa> fines = service.getAll();

        // 2. Dati remoti (Microservizio Pagamenti)
        // Nel MultaController del servizio Multa (porta 8088)
        List<PagamentoDTO> payments = webClient.get()
                .uri("https://localhost:8087/payments") // Porta 8087 come da tue properties
                .retrieve()
                .bodyToFlux(PagamentoDTO.class)
                .collectList()
                .block(Duration.ofSeconds(3));

        // 3. Merge
        System.out.println(mergeData(fines, payments));
        return ResponseEntity.ok(mergeData(fines, payments));
    }

    private List<MultaDTO> mergeData(List<Multa> fines, List<PagamentoDTO> payments) {
        Map<Integer, PagamentoDTO> payMap = payments.stream()
                .filter(p -> p.getIdBiglietto() != null)
                .collect(Collectors.toMap(
                        p -> p.getIdBiglietto().intValue(),
                        p -> p,
                        (existing, replacement) -> existing
                ));

        return fines.stream()
                .filter(f -> f.getIdBiglietto() != null && payMap.containsKey(f.getIdBiglietto()))
                .map(f -> {
                    PagamentoDTO p = payMap.get(f.getIdBiglietto());
                    return new MultaDTO(
                            f.getId().longValue(),
                            f.getTarga(),
                            f.getImporto(),
                            p.getStato(),
                            p.getTimestampOut(),
                            f.getIdBiglietto().longValue()
                    );
                })
                .collect(Collectors.toList());
    }

    // Il fallback deve avere la stessa firma (ResponseEntity<List<MultaDTO>>)
    public ResponseEntity<List<MultaDTO>> fallbackPayments(Exception e) {
        System.err.println("Fallback attivato: il servizio pagamenti è offline.");
        List<MultaDTO> localOnly = service.getAll().stream()
                .map(f -> new MultaDTO(f, "STATO NON DISPONIBILE"))
                .collect(Collectors.toList());
        return ResponseEntity.ok(localOnly);
    }
}
