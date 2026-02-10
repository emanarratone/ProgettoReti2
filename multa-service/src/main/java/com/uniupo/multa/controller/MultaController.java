package com.uniupo.multa.controller;

import com.uniupo.multa.model.Multa;
import com.uniupo.multa.model.dto.MultaDTO;
import com.uniupo.multa.model.dto.MultaGestionaleDTO;
import com.uniupo.multa.model.dto.PagamentoDTO;
import com.uniupo.multa.service.MultaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/fines")
public class MultaController {

    private final MultaService service;
    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(MultaController.class);


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
                .uri("https://localhost:8087/payments")
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

    @GetMapping("/gestione-completa")
    @CircuitBreaker(name = "fullAggregation", fallbackMethod = "fallbackGestione")
    public ResponseEntity<List<MultaGestionaleDTO>> getGestioneCompleta() {
        // 1. Dati locali (DB Multe)
        List<Multa> fines = service.getAll();

        // 2. Recupero dati remoti dai microservizi esterni
        List<Map<String, Object>> payments = fetchData("https://localhost:8087/payments");
        List<Map<String, Object>> tolls = fetchData("https://localhost:8082/tolls");
        List<Map<String, Object>> highways = fetchData("https://localhost:8081/highways");
        List<Map<String, Object>> regions = fetchData("https://localhost:8084/regions");

        // 3. Merge e Ritorno
        List<MultaGestionaleDTO> merged = mergeFullData(fines, payments, tolls, highways, regions);
        return ResponseEntity.ok(merged);
    }


    // Helper per pulire il codice
    private List<Map<String, Object>> fetchData(String url) {
        return webClient.get().uri(url).retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block(Duration.ofSeconds(2));
    }

    // Fallback se uno dei servizi esterni è offline
    public ResponseEntity<List<MultaGestionaleDTO>> fallbackGestione(Exception e) {
        logger.error("🚨 Fallback attivato! Errore nell'aggregazione dati: {}", e.getMessage());

        List<MultaGestionaleDTO> fallbackList = service.getAll().stream()
                .map(f -> new MultaGestionaleDTO(
                        f.getId(),             // ID Multa (Locale)
                        "SERVIZIO OFFLINE",    // Nome Regione (Esterno)
                        "N/D",                 // Nome Casello (Esterno)
                        f.getTarga(),          // Targa (Locale)
                        "N/D",                 // Data Pagamento (Esterno)
                        f.getImporto(),        // Importo (Locale)
                        f.getPagato() ? "PAGATA (L)" : "DA PAGARE" // Stato (Locale)
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(fallbackList);
    }

    private List<MultaGestionaleDTO> mergeFullData(
            List<Multa> fines,
            List<Map<String, Object>> payments,
            List<Map<String, Object>> tolls,
            List<Map<String, Object>> highways,
            List<Map<String, Object>> regions) {

        // Creazione delle mappe di lookup (come prima)
        Map<Integer, Map<String, Object>> payMap = payments.stream()
                .collect(Collectors.toMap(p -> (Integer)p.get("idBiglietto"), p -> p, (a,b)->a));
        Map<Integer, Map<String, Object>> tollMap = tolls.stream()
                .collect(Collectors.toMap(t -> (Integer)t.get("idCasello"), t -> t, (a,b)->a));
        Map<Integer, Map<String, Object>> highwayMap = highways.stream()
                .collect(Collectors.toMap(h -> (Integer)h.get("id"), h -> h, (a,b)->a));
        Map<Integer, String> regionMap = regions.stream()
                .collect(Collectors.toMap(r -> (Integer)r.get("id"), r -> (String)r.get("nome"), (a,b)->a));

        // Trasformazione con FILTRO (Inner Join)
        return fines.stream()
                .filter(f -> payMap.containsKey(f.getIdBiglietto())) // 1. Scarta se non c'è pagamento
                .map(f -> {
                    Map<String, Object> p = payMap.get(f.getIdBiglietto());
                    Integer idC = (Integer) p.get("caselloOut");

                    // 2. Verifica se esiste il casello, l'autostrada e la regione
                    Map<String, Object> t = tollMap.get(idC);
                    if (t == null) return null; // Scarta se il casello non esiste

                    Integer idA = (Integer) t.get("idAutostrada");
                    Map<String, Object> h = highwayMap.get(idA);
                    if (h == null) return null; // Scarta se l'autostrada non esiste

                    Integer idR = (Integer) h.get("idRegione");
                    String nomeRegione = regionMap.get(idR);
                    if (nomeRegione == null) return null; // Scarta se la regione non esiste

                    // Se siamo arrivati qui, tutti i pezzi del puzzle esistono
                    MultaGestionaleDTO dto = new MultaGestionaleDTO();
                    dto.setId(f.getId());
                    dto.setTarga(f.getTarga());
                    dto.setImporto(f.getImporto());
                    dto.setData(p.get("timestampOut").toString());
                    dto.setStato((String) p.get("stato"));
                    dto.setNomeCasello((String) t.get("sigla"));
                    dto.setNomeRegione(nomeRegione);
                    return dto;
                })
                .filter(Objects::nonNull) // Rimuove i record che hanno restituito null durante il map
                .collect(Collectors.toList());
    }
}
