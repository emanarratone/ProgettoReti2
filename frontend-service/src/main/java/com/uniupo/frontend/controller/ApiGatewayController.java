package com.uniupo.frontend.controller;

import com.uniupo.frontend.config.WebConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        // Deve puntare a /payments (plurale), come definito nel microservizio
        return forwardGet(webConfig.getPagamentoUrl() + "/payments");
    }

    @GetMapping("/payments/biglietto/{idBiglietto}")
    public ResponseEntity<?> getPaymentsByTicket(@PathVariable Integer idBiglietto) {
        // Punta all'endpoint specifico del microservizio
        return forwardGet(webConfig.getPagamentoUrl() + "/payments/biglietto/" + idBiglietto);
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

    @GetMapping("/vehicles/history")
    public ResponseEntity<?> getVehicleHistory(@RequestParam String plate) {
        try {
            // 1. Carica nomi caselli
            Map<Integer, String> tollNames = fetchTollMap();

            // 2. Recupera Biglietti
            String ticketUrl = webConfig.getBigliettoUrl() + "/tickets/targa/" + plate;
            ResponseEntity<List<Map<String, Object>>> ticketsResp = restTemplate.exchange(
                    ticketUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<String, Object>>>() {});

            List<Map<String, Object>> tickets = ticketsResp.getBody();
            List<Map<String, Object>> history = new ArrayList<>();

            if (tickets != null) {
                for (Map<String, Object> t : tickets) {
                    Map<String, Object> row = new HashMap<>();

                    // Biglietto ID
                    Object idBObj = t.get("id_biglietto") != null ? t.get("id_biglietto") : t.get("idBiglietto");
                    Integer idB = ((Number) idBObj).intValue();

                    // TRADUZIONE CASELLO INGRESSO
                    Object idIn = t.get("casello_in") != null ? t.get("casello_in") : t.get("caselloIn");
                    String nomeIn = tollNames.getOrDefault(((Number) idIn).intValue(), "Casello " + idIn);

                    row.put("dataIngresso", t.get("timestamp_in") != null ? t.get("timestamp_in") : t.get("timestampIn"));
                    row.put("siglaIngresso", nomeIn);

                    // 3. Recupero Pagamento
                    try {
                        String pUrl = webConfig.getPagamentoUrl() + "/payments/biglietto/" + idB;
                        ResponseEntity<List<Map<String, Object>>> pResp = restTemplate.exchange(
                                pUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<String, Object>>>() {});

                        List<Map<String, Object>> payments = pResp.getBody();
                        if (payments != null && !payments.isEmpty()) {
                            Map<String, Object> p = payments.get(0);

                            // TRADUZIONE CASELLO USCITA
                            Object idOut = p.get("casello_out") != null ? p.get("casello_out") : p.get("caselloOut");
                            String nomeOut = tollNames.getOrDefault(((Number) idOut).intValue(), "Casello " + idOut);

                            row.put("dataUscita", p.get("timestamp_out") != null ? p.get("timestamp_out") : p.get("timestampOut"));
                            row.put("siglaUscita", nomeOut);
                            row.put("importo", p.get("importo"));
                            row.put("stato", "PAGATO");
                        } else {
                            row.put("dataUscita", null);
                            row.put("siglaUscita", "-");
                            row.put("importo", 0.0);
                            row.put("stato", "IN VIAGGIO");
                        }
                    } catch (Exception e) {
                        row.put("stato", "ERRORE");
                    }
                    history.add(row);
                }
            }
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore: " + e.getMessage());
        }
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

    @GetMapping("/tolls")
    public ResponseEntity<?> getTolls() {
        // Cambiato da "/toll" a "/tolls" per coincidere con il microservizio
        String url = webConfig.getCaselloUrl() + "/tolls";
        logger.info("Forwarding to Casello Service: {}", url);
        return forwardGet(url);
    }

    // 2. Endpoint per la tabella Multe (usato da fetch('/api/fines/list'))
    @GetMapping("/fines/list")
    public ResponseEntity<?> getFinesList() {
        // Inoltra alla lista completa delle multe
        return forwardGet(webConfig.getMultaUrl() + "/fines");
    }

    @GetMapping("/tickets/traffic")
    public ResponseEntity<?> getTicketsTraffic() {
        logger.info("🔥 Gateway → biglietti-service/traffic");
        return forwardGet(webConfig.getBigliettoUrl() + "/tickets/traffic");
    }


    @GetMapping("/traffic/trend")
    public ResponseEntity<?> getTrafficTrend() {
        ResponseEntity<?> resp = forwardGet(webConfig.getBigliettoUrl() + "/traffic/trend");
        if (resp.getStatusCode().is2xxSuccessful()) return resp;
        logger.warn("/api/traffic/trend not available, returning empty trend fallback (status {})", resp.getStatusCode());
        return ResponseEntity.ok(new Object[0]);
    }

    @GetMapping("/traffic/24hours")
    public ResponseEntity<?> getTraffic24Hours() {
        return forwardGet(webConfig.getBigliettoUrl() + "/tickets/traffic/24hours");
    }

    @GetMapping("/traffic/30days")
    public ResponseEntity<?> getTraffic30Days() {
        return forwardGet(webConfig.getBigliettoUrl() + "/tickets/traffic/30days");
    }

    @GetMapping("/assets")
    public ResponseEntity<?> getAssets() {

        int caselli = 0;
        int corsie = 0;
        int dispositivi = 0;

        try {
            String caselloUrl = webConfig.getCaselloUrl() + "/tolls";
            logger.info("Casello URL: {}", caselloUrl);

            ResponseEntity<Object[]> caselliResp = restTemplate.getForEntity(caselloUrl, Object[].class);
            logger.info("Casello response: status={}, body.length={}",
                    caselliResp.getStatusCode(),
                    caselliResp.getBody() != null ? caselliResp.getBody().length : "null");

            if (caselliResp.getStatusCode().is2xxSuccessful() && caselliResp.getBody() != null) {
                caselli = caselliResp.getBody().length;
            }
        } catch (Exception e) {
            logger.error("ERRORE casello: {}", e.getMessage(), e);
        }

        try {
            ResponseEntity<Object[]> corsieResp =
                    restTemplate.getForEntity(webConfig.getCorsiaUrl() + "/lanes", Object[].class);
            if (corsieResp.getStatusCode().is2xxSuccessful() && corsieResp.getBody() != null) {
                corsie = corsieResp.getBody().length;

            }
        } catch (Exception e) {
            logger.warn("Error querying corsia-service for /lanes: {}", e.getMessage());
        }

        try {
            ResponseEntity<Object[]> dispResp =
                    restTemplate.getForEntity(webConfig.getDispositiviUrl() + "/devices", Object[].class);
            if (dispResp.getStatusCode().is2xxSuccessful() && dispResp.getBody() != null) {
                dispositivi = dispResp.getBody().length;
            }
        } catch (Exception e) {
            logger.warn("Error querying dispositivi-service for /devices: {}", e.getMessage());
        }
        logger.info("Assets finali: caselli={}, corsie={}, dispositivi={}", caselli, corsie, dispositivi);
        return ResponseEntity.ok(
                Map.of(
                        "caselli", caselli,
                        "corsie", corsie,
                        "dispositivi", dispositivi
                )
        );
    }

    // KPI endpoints that compute from microservices when legacy not present
    @GetMapping("/fines")
    public ResponseEntity<?> getFinesCount() {
        // try legacy first
        ResponseEntity<?> legacy = forwardGet(webConfig.getAutostradaUrl() + "/api/fines");
        if (legacy.getStatusCode().is2xxSuccessful()) return legacy;

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
        ResponseEntity<?> legacy = forwardGet(webConfig.getPagamentoUrl() + "/api/payments");
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

    @GetMapping("/regions/search")
    public ResponseEntity<?> searchRegions(@RequestParam("q") String q) {
        return forwardGet(webConfig.getRegioneUrl() + "/regions/search?q=" + q);
    }

    @PostMapping("/regions")
    public ResponseEntity<?> createRegion(@RequestBody Object body) {
        return forwardPost(webConfig.getRegioneUrl() + "/regions", body);
    }

    @PutMapping("/regions/{id}")
    public ResponseEntity<?> updateRegion(@PathVariable Integer id, @RequestBody Object body) {
        return forwardPut(webConfig.getRegioneUrl() + "/regions/" + id, body);
    }

    @DeleteMapping("/regions/{id}")
    public ResponseEntity<?> deleteRegion(@PathVariable Integer id) {
        return forwardDelete(webConfig.getRegioneUrl() + "/regions/" + id);
    }

    @GetMapping("/regions/{id}/highways")
    public ResponseEntity<?> getHighwaysForRegion(@PathVariable Integer id) {
        // forwards to autostrada-service which now exposes /regions/{id}/highways
        return forwardGet(webConfig.getAutostradaUrl() + "/regions/" + id + "/highways");
    }

    @GetMapping("/tolls/{id}/lanes")
    public ResponseEntity<?> getLanesForToll(@PathVariable Integer id) {
        // forward to corsia-service endpoint we just added
        return forwardGet(webConfig.getCorsiaUrl() + "/tolls/" + id + "/lanes");
    }

    // ---- CASELLI (highway -> tolls)
    @GetMapping("/highways/{id}/tolls")
    public ResponseEntity<?> getTollsForHighway(@PathVariable Integer id) {
        return forwardGet(webConfig.getCaselloUrl() + "/highways/" + id + "/tolls");
    }

    @PostMapping("/highways/{id}/tolls")
    public ResponseEntity<?> createTollForHighway(@PathVariable Integer id, @RequestBody Object body) {
        return forwardPost(webConfig.getCaselloUrl() + "/highways/" + id + "/tolls", body);
    }

    @PutMapping("/tolls/{id}")
    public ResponseEntity<?> updateToll(@PathVariable Integer id, @RequestBody Object body) {
        return forwardPut(webConfig.getCaselloUrl() + "/tolls/" + id, body); // aggiunto 's'
    }

    @DeleteMapping("/tolls/{id}")
    public ResponseEntity<?> deleteToll(@PathVariable Integer id) {
        return forwardDelete(webConfig.getCaselloUrl() + "/tolls/" + id); // aggiunto 's'
    }

    // ---- CORSIE legacy-style routing
    @PostMapping("/tolls/{id}/lanes")
    public ResponseEntity<?> createLaneForToll(@PathVariable Integer id, @RequestBody Object body) {
        return forwardPost(webConfig.getCorsiaUrl() + "/tolls/" + id + "/lanes", body);
    }

    @PutMapping("/lanes/{idCasello}/{numCorsia}")
    public ResponseEntity<?> updateLane(@PathVariable Integer idCasello, @PathVariable Integer numCorsia, @RequestBody Object body) {
        return forwardPut(webConfig.getCorsiaUrl() + "/lanes/" + idCasello + "/" + numCorsia, body);
    }

    @DeleteMapping("/lanes/{idCasello}/{numCorsia}")
    public ResponseEntity<?> deleteLane(@PathVariable Integer idCasello, @PathVariable Integer numCorsia) {
        return forwardDelete(webConfig.getCorsiaUrl() + "/lanes/" + idCasello + "/" + numCorsia);
    }

    // ---- DISPOSITIVI per corsia
    @GetMapping("/lanes/{idCasello}/{numCorsia}/devices")
    public ResponseEntity<?> getDevicesForLane(@PathVariable Integer idCasello, @PathVariable Integer numCorsia) {
        return forwardGet(webConfig.getDispositiviUrl() + "/lanes/" + idCasello + "/" + numCorsia + "/devices");
    }

    @PostMapping("/lanes/{idCasello}/{numCorsia}/devices")
    public ResponseEntity<?> createDeviceForLane(@PathVariable Integer idCasello, @PathVariable Integer numCorsia, @RequestBody Object body) {
        return forwardPost(webConfig.getDispositiviUrl() + "/lanes/" + idCasello + "/" + numCorsia + "/devices", body);
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
            logger.info("Gateway DELETE forwarding to: {}", url);
            restTemplate.delete(url);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (HttpStatusCodeException e) {
            logger.warn("Backend DELETE error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Error forwarding DELETE to {}: {}", url, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Service unavailable: " + e.getMessage()));
        }
    }

    @PutMapping("/devices/{id}")
    public ResponseEntity<?> updateDevice(@PathVariable Integer id, @RequestBody Object body) {
        String url = webConfig.getDispositiviUrl() + "/devices/" + id;
        return forwardPut(url, body);
    }

    @DeleteMapping("/devices/{id}")
    public ResponseEntity<?> deleteDevice(@PathVariable Integer id) {
        return forwardDelete(webConfig.getDispositiviUrl() + "/devices/" + id);
    }

    private Map<Integer, String> fetchTollMap() {
        Map<Integer, String> tollMap = new HashMap<>();
        try {
            // Costruisce l'URL puntando all'endpoint globale dei caselli
            String tollUrl = webConfig.getCaselloUrl() + "/tolls";

            // Esegue la chiamata REST per ottenere la lista di tutti i caselli
            ResponseEntity<List<Map<String, Object>>> tollsResp = restTemplate.exchange(
                    tollUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            if (tollsResp.getBody() != null) {
                for (Map<String, Object> t : tollsResp.getBody()) {
                    // Gestisce la mappatura cercando l'ID sia come 'id_casello' che come 'idCasello'
                    Object idObj = t.get("id_casello") != null ? t.get("id_casello") : t.get("idCasello");
                    if (idObj instanceof Number) {
                        int idVal = ((Number) idObj).intValue();
                        String sigla = (String) t.get("sigla");
                        tollMap.put(idVal, sigla);
                    }
                }
            }
            logger.info("Mappa caselli caricata con successo: {} elementi", tollMap.size());
        } catch (Exception e) {
            // Se il servizio caselli è offline, il sistema logga l'errore e restituisce una mappa vuota
            logger.error("Impossibile caricare la mappa dei caselli: {}", e.getMessage());
        }
        return tollMap;
    }
}
