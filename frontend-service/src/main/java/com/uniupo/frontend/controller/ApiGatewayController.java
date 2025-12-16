package com.uniupo.frontend.controller;

import com.uniupo.frontend.config.WebConfig;
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
    public ResponseEntity<?> login(@RequestBody Object credentials, jakarta.servlet.http.HttpSession session) {
        ResponseEntity<?> response = forwardPost(webConfig.getUtenteUrl() + "/users/login", credentials);
        
        // Se login OK, salva in sessione
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> body = (Map<String, Object>) response.getBody();
                if (body.get("success") != null && (Boolean) body.get("success")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> user = (Map<String, Object>) body.get("user");
                    if (user != null) {
                        session.setAttribute("user", user.get("username"));
                        session.setAttribute("isAdmin", user.get("isAdmin"));
                    }
                }
            } catch (Exception e) {
                logger.error("Errore nel salvataggio sessione: {}", e.getMessage());
            }
        }
        
        return response;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Object user) {
        return forwardPost(webConfig.getUtenteUrl() + "/users", user);
    }

    @GetMapping("/users/**")
    public ResponseEntity<?> getUsers() {
        return forwardGet(webConfig.getUtenteUrl() + "/users");
    }

    @GetMapping("/session")
    public ResponseEntity<?> getSession(jakarta.servlet.http.HttpSession session) {
        Object user = session.getAttribute("user");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        if (user != null) {
            return ResponseEntity.ok(Map.of(
                "loggedIn", true,
                "username", user,
                "isAdmin", isAdmin != null ? isAdmin : false
            ));
        }
        return ResponseEntity.ok(Map.of("loggedIn", false));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(jakarta.servlet.http.HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ================== LEGACY DASHBOARD ENDPOINTS (with dev fallback) ==================
    @GetMapping("/tolls")
    public ResponseEntity<?> getTolls() {
        ResponseEntity<?> resp = forwardGet(webConfig.getLegacyUrl() + "/api/tolls");
        if (resp.getStatusCode().is2xxSuccessful()) return resp;
        logger.warn("Legacy /api/tolls not available, returning empty list fallback (status {})", resp.getStatusCode());
        return ResponseEntity.ok(new Object[0]);
    }

    @GetMapping("/traffic")
    public ResponseEntity<?> getTrafficKpi() {
        ResponseEntity<?> resp = forwardGet(webConfig.getLegacyUrl() + "/api/traffic");
        if (resp.getStatusCode().is2xxSuccessful()) return resp;
        logger.warn("Legacy /api/traffic not available, returning fallback KPI (status {})", resp.getStatusCode());
        return ResponseEntity.ok(Map.of("media", 0, "oggi", 0, "variazione", 0.0));
    }

    @GetMapping("/traffic/trend")
    public ResponseEntity<?> getTrafficTrend() {
        ResponseEntity<?> resp = forwardGet(webConfig.getLegacyUrl() + "/api/traffic/trend");
        if (resp.getStatusCode().is2xxSuccessful()) return resp;
        logger.warn("Legacy /api/traffic/trend not available, returning empty trend fallback (status {})", resp.getStatusCode());
        return ResponseEntity.ok(new Object[0]);
    }

    @GetMapping("/traffic/peaks")
    public ResponseEntity<?> getTrafficPeaks() {
        ResponseEntity<?> resp = forwardGet(webConfig.getLegacyUrl() + "/api/traffic/peaks");
        if (resp.getStatusCode().is2xxSuccessful()) return resp;
        logger.warn("Legacy /api/traffic/peaks not available, returning empty peaks fallback (status {})", resp.getStatusCode());
        return ResponseEntity.ok(new Object[0]);
    }

    @GetMapping("/assets")
    public ResponseEntity<?> getAssets() {
        ResponseEntity<?> resp = forwardGet(webConfig.getLegacyUrl() + "/api/assets");
        if (resp.getStatusCode().is2xxSuccessful()) return resp;
        logger.warn("Legacy /api/assets not available, returning zeroed assets fallback (status {})", resp.getStatusCode());
        return ResponseEntity.ok(Map.of("caselli", 0, "corsie", 0, "dispositivi", 0));
    }

    @GetMapping("/fines/list")
    public ResponseEntity<?> getFinesList() {
        ResponseEntity<?> resp = forwardGet(webConfig.getLegacyUrl() + "/api/fines/list");
        if (resp.getStatusCode().is2xxSuccessful()) return resp;
        logger.warn("Legacy /api/fines/list not available, returning empty list fallback (status {})", resp.getStatusCode());
        return ResponseEntity.ok(new Object[0]);
    }

    // KPI endpoints that compute from microservices when legacy not present
    @GetMapping("/fines")
    public ResponseEntity<?> getFinesCount() {
        // try legacy first
        ResponseEntity<?> legacy = forwardGet(webConfig.getLegacyUrl() + "/api/fines");
        if (legacy.getStatusCode().is2xxSuccessful()) return legacy;

        // fallback: ask multa-service for list and return count
        try {
            ResponseEntity<Object[]> listResp = restTemplate.getForEntity(webConfig.getMultaUrl() + "/fines", Object[].class);
            if (listResp.getStatusCode().is2xxSuccessful() && listResp.getBody() != null) {
                return ResponseEntity.ok(Map.of("fines", listResp.getBody().length));
            }
        } catch (Exception e) {
            logger.warn("Error querying multa-service for /fines: {}", e.getMessage());
        }
        return ResponseEntity.ok(Map.of("fines", 0));
    }

    @GetMapping("/payments")
    public ResponseEntity<?> getPaymentsPending() {
        // try legacy first
        ResponseEntity<?> legacy = forwardGet(webConfig.getLegacyUrl() + "/api/payments");
        if (legacy.getStatusCode().is2xxSuccessful()) return legacy;

        // fallback: ask pagamento-service /payments/unpaid
        try {
            ResponseEntity<Object[]> listResp = restTemplate.getForEntity(webConfig.getPagamentoUrl() + "/payments/unpaid", Object[].class);
            if (listResp.getStatusCode().is2xxSuccessful() && listResp.getBody() != null) {
                return ResponseEntity.ok(Map.of("pending", listResp.getBody().length));
            }
        } catch (Exception e) {
            logger.warn("Error querying pagamento-service for /payments/unpaid: {}", e.getMessage());
        }
        return ResponseEntity.ok(Map.of("pending", 0));
    }

    @GetMapping("/regions")
    public ResponseEntity<?> getRegions() {
        return forwardGet(webConfig.getRegioneUrl() + "/regions");
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
