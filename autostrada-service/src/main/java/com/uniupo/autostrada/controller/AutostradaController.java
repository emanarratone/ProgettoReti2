package com.uniupo.autostrada.controller;

import com.uniupo.autostrada.model.Autostrada;
import com.uniupo.autostrada.model.dto.AutostradaCreateUpdateDTO;
import com.uniupo.autostrada.model.dto.AutostradaDTO;
import com.uniupo.autostrada.service.AutostradaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/highways")
public class AutostradaController {

    private final AutostradaService service;
    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(AutostradaController.class);

    public AutostradaController(AutostradaService service, WebClient webClient){
        this.service = service;
        this.webClient = webClient;
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

    @GetMapping("/summary")
    @CircuitBreaker(name = "regioneService", fallbackMethod = "fallbackSummary")
    public ResponseEntity<List<Map<String, Object>>> getHighwaysSummary() {
        // 1. Dati locali (DB Autostrada) - Prendiamo le 5 uniche
        List<AutostradaDTO> highways = service.getTop5Unique();

        // 2. Recupero dati remoti (Nomi Regioni) usando l'helper fetchData
        // Assicurati che la porta 8084 sia quella del microservizio Regione
        List<Map<String, Object>> regions = fetchData("https://localhost:8084/regions");

        // 3. Merge e Ritorno
        return ResponseEntity.ok(mergeHighwaysAndRegions(highways, regions));
    }

    // Helper per pulire il codice (esattamente come nel MultaController)
    private List<Map<String, Object>> fetchData(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block(Duration.ofSeconds(2));
    }

    private List<Map<String, Object>> mergeHighwaysAndRegions(
            List<AutostradaDTO> highways,
            List<Map<String, Object>> regions) {

        // Mappa di lookup ID -> Nome Regione
        Map<Integer, String> regionMap = regions.stream()
                .collect(Collectors.toMap(
                        r -> (Integer) r.get("id"),
                        r -> (String) r.get("nome"),
                        (existing, replacement) -> existing
                ));

        return highways.stream()
                .map(h -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("sigla", h.getSigla());
                    map.put("regioneNome", regionMap.getOrDefault(h.getIdRegione(), "Sconosciuta"));
                    return map;
                })
                .collect(Collectors.toList());
    }

    // Fallback se il servizio Regioni è offline
    public ResponseEntity<List<Map<String, Object>>> fallbackSummary(Exception e) {
        logger.error("🚨 Fallback Autostrada: Servizio Regioni offline. Dettaglio: {}", e.getMessage());

        List<Map<String, Object>> fallbackList = service.getTop5Unique().stream()
                .map(h -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("sigla", h.getSigla());
                    map.put("regioneNome", "N/D (Offline)");
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(fallbackList);
    }
}
