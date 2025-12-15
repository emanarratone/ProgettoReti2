package com.example.frontend.controller;

import com.example.frontend.config.WebConfig;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiGatewayController {

    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayController.class);

    private final RestTemplate restTemplate;
    private final WebConfig webConfig;

    public ApiGatewayController(RestTemplate restTemplate, WebConfig webConfig) {
        this.restTemplate = restTemplate;
        this.webConfig = webConfig;
    }

    // ================== AUTOSTRADA ==================
    @GetMapping("/highways/**")
    public ResponseEntity<?> getHighways(@RequestParam(required = false) Map<String, String> params) {
        String url = webConfig.getAutostradaUrl() + "/highways";
        if (params.containsKey("q")) {
            url += "/search?q=" + params.get("q");
        }
        return forwardGet(url);
    }

    @PostMapping("/highways")
    public ResponseEntity<?> createHighway(@RequestBody Object body) {
        return forwardPost(webConfig.getAutostradaUrl() + "/highways", body);
    }

    @PutMapping("/highways/{id}")
    public ResponseEntity<?> updateHighway(@PathVariable Integer id, @RequestBody Object body) {
        return forwardPut(webConfig.getAutostradaUrl() + "/highways/" + id, body);
    }

    @DeleteMapping("/highways/{id}")
    public ResponseEntity<?> deleteHighway(@PathVariable Integer id) {
        return forwardDelete(webConfig.getAutostradaUrl() + "/highways/" + id);
    }

    // ================== CASELLO ==================
    @GetMapping("/toll/**")
    public ResponseEntity<?> getTollBooths() {
        return forwardGet(webConfig.getCaselloUrl() + "/toll");
    }

    @PostMapping("/toll")
    public ResponseEntity<?> createTollBooth(@RequestBody Object body) {
        return forwardPost(webConfig.getCaselloUrl() + "/toll", body);
    }

    // ================== BIGLIETTO ==================
    @GetMapping("/tickets/**")
    public ResponseEntity<?> getTickets() {
        return forwardGet(webConfig.getBigliettoUrl() + "/tickets");
    }

    @PostMapping("/tickets")
    public ResponseEntity<?> createTicket(@RequestBody Object body) {
        return forwardPost(webConfig.getBigliettoUrl() + "/tickets", body);
    }

    // ================== PAGAMENTO ==================
    @GetMapping("/payments/**")
    public ResponseEntity<?> getPayments() {
        return forwardGet(webConfig.getPagamentoUrl() + "/payments");
    }

    @PostMapping("/payments")
    public ResponseEntity<?> createPayment(@RequestBody Object body) {
        return forwardPost(webConfig.getPagamentoUrl() + "/payments", body);
    }

    // ================== MULTA ==================
    @GetMapping("/fines/**")
    public ResponseEntity<?> getFines() {
        return forwardGet(webConfig.getMultaUrl() + "/fines");
    }

    @PostMapping("/fines")
    public ResponseEntity<?> createFine(@RequestBody Object body) {
        return forwardPost(webConfig.getMultaUrl() + "/fines", body);
    }

    // ================== VEICOLO ==================
    @GetMapping("/vehicles/**")
    public ResponseEntity<?> getVehicles() {
        return forwardGet(webConfig.getVeicoloUrl() + "/vehicles");
    }

    @PostMapping("/vehicles")
    public ResponseEntity<?> createVehicle(@RequestBody Object body) {
        return forwardPost(webConfig.getVeicoloUrl() + "/vehicles", body);
    }

    // ================== UTENTE ==================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Object credentials) {
        return forwardPost(webConfig.getUtenteUrl() + "/users/login", credentials);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Object user) {
        return forwardPost(webConfig.getUtenteUrl() + "/users", user);
    }

    @GetMapping("/users/**")
    public ResponseEntity<?> getUsers() {
        return forwardGet(webConfig.getUtenteUrl() + "/users");
    }

    // ================== HELPER METHODS ==================
    private ResponseEntity<?> forwardGet(String url) {
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpStatusCodeException e) {
            logger.warn("Backend GET returned status {} for {}: {}", e.getStatusCode(), url, e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Error forwarding GET to {}: {}", url, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Service unavailable: " + e.getMessage()));
        }
    }

    private ResponseEntity<?> forwardPost(String url, Object body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> request = new HttpEntity<>(body, headers);
            ResponseEntity<Object> response = restTemplate.postForEntity(url, request, Object.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpStatusCodeException e) {
            logger.warn("Backend POST returned status {} for {}: {}", e.getStatusCode(), url, e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Error forwarding POST to {}: {}", url, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Service unavailable: " + e.getMessage()));
        }
    }

    private ResponseEntity<?> forwardPut(String url, Object body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> request = new HttpEntity<>(body, headers);
            restTemplate.put(url, request);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (HttpStatusCodeException e) {
            logger.warn("Backend PUT returned status {} for {}: {}", e.getStatusCode(), url, e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Error forwarding PUT to {}: {}", url, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Service unavailable: " + e.getMessage()));
        }
    }

    private ResponseEntity<?> forwardDelete(String url) {
        try {
            restTemplate.delete(url);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (HttpStatusCodeException e) {
            logger.warn("Backend DELETE returned status {} for {}: {}", e.getStatusCode(), url, e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Error forwarding DELETE to {}: {}", url, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Service unavailable: " + e.getMessage()));
        }
    }
}
