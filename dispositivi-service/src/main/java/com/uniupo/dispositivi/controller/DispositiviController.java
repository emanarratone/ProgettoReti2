package com.uniupo.dispositivi.controller;

import com.uniupo.dispositivi.model.Dispositivo;
import com.uniupo.shared.mqtt.dto.richiestaPagamentoEvent;
import com.uniupo.dispositivi.service.DispositiviService;
import com.uniupo.shared.mqtt.dto.RichiestaBigliettoEvent;
import com.uniupo.shared.mqtt.MqttMessageBroker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
public class DispositiviController {

    private final DispositiviService service;
    private final MqttMessageBroker mqttBroker;

    public DispositiviController(DispositiviService service, MqttMessageBroker mqttBroker) {
        this.service = service;
        this.mqttBroker = mqttBroker;
    }

    // ------------------- HEALTH -------------------

    @GetMapping("/devices/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "dispositivi-service"));
    }

    // ------------------- CRUD GLOBAL /devices -------------------

    @GetMapping("/devices")
    public ResponseEntity<List<Dispositivo>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/devices/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/devices")
    public ResponseEntity<?> create(@RequestBody Dispositivo dispositivo) {
        try {
            Dispositivo saved = service.create(dispositivo);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione dispositivo"));
        }
    }

    @PutMapping("/devices/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            Boolean status;
            if (body.containsKey("stato")) {
                status = "ATTIVO".equalsIgnoreCase(String.valueOf(body.get("stato")))
                        || Boolean.TRUE.equals(body.get("stato"));
            } else if (body.containsKey("status")) {
                status = Boolean.TRUE.equals(body.get("status"));
            } else {
                status = null;
            }

            Integer casello = body.containsKey("casello") ? ((Number) body.get("casello")).intValue() : null;
            Integer corsia  = body.containsKey("corsia")  ? ((Number) body.get("corsia")).intValue()  : null;

            return service.getById(id).map(existing -> {
                if (status != null) existing.setStatus(status);
                if (casello != null) existing.setCasello(casello);
                if (corsia  != null) existing.setCorsia(corsia);
                Dispositivo saved = service.create(existing);
                return ResponseEntity.ok(saved);
            }).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore aggiornamento dispositivo"));
        }
    }

    @PutMapping("/devices/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id,
                                          @RequestBody Map<String, Boolean> body) {
        try {
            Boolean status = body.get("status");
            service.updateStatus(id, status);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore aggiornamento stato"));
        }
    }

    @DeleteMapping("/devices/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore cancellazione dispositivo"));
        }
    }

    // ------------------- QUERY /devices/... -------------------

    @GetMapping("/devices/sbarre")
    public ResponseEntity<?> getSbarre() {
        return ResponseEntity.ok(service.getSbarre());
    }

    @GetMapping("/devices/telecamere")
    public ResponseEntity<?> getTelecamere() {
        return ResponseEntity.ok(service.getTelecamere());
    }

    @GetMapping("/devices/totem")
    public ResponseEntity<?> getTotem() {
        return ResponseEntity.ok(service.getTotem());
    }

    @GetMapping("/devices/casello/{casello}")
    public ResponseEntity<List<Dispositivo>> getByCasello(@PathVariable Integer casello) {
        return ResponseEntity.ok(service.getByCasello(casello));
    }

    @GetMapping("/devices/corsia/{corsia}")
    public ResponseEntity<List<Dispositivo>> getByCorsia(@PathVariable Integer corsia) {
        return ResponseEntity.ok(service.getByCorsia(corsia));
    }

    @GetMapping("/devices/active")
    public ResponseEntity<List<Dispositivo>> getActive() {
        return ResponseEntity.ok(service.getActive());
    }

    @GetMapping("/devices/inactive")
    public ResponseEntity<List<Dispositivo>> getInactive() {
        return ResponseEntity.ok(service.getInactive());
    }

    @GetMapping("/devices/search")
    public ResponseEntity<?> search(@RequestParam("q") String q) {
        try {
            if (q == null || q.isBlank())
                return ResponseEntity.badRequest().body(Map.of("error", "q obbligatorio"));

            try {
                Integer id = Integer.parseInt(q);
                return ResponseEntity.ok(service.getByCasello(id));
            } catch (NumberFormatException ignored) {}

            String s = q.toLowerCase();
            if (s.contains("totem"))       return ResponseEntity.ok(service.getTotem());
            if (s.contains("telecamera"))  return ResponseEntity.ok(service.getTelecamere());
            if (s.contains("sbarra"))      return ResponseEntity.ok(service.getSbarre());
            if (s.contains("attiv") || s.contains("active"))   return ResponseEntity.ok(service.getActive());
            if (s.contains("inatt") || s.contains("inactive")) return ResponseEntity.ok(service.getInactive());

            return ResponseEntity.badRequest().body(Map.of("error", "q non riconosciuto"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore ricerca dispositivi"));
        }
    }

    // ------------------- NESTED: /lanes/{idCasello}/{numCorsia}/devices -------------------

    @GetMapping("/lanes/{idCasello}/{numCorsia}/devices")
    public ResponseEntity<?> getDevicesForLane(@PathVariable Integer idCasello,
                                               @PathVariable Integer numCorsia) {
        return ResponseEntity.ok(service.getByCaselloAndCorsia(idCasello, numCorsia));
    }

    @PostMapping("/lanes/{idCasello}/{numCorsia}/devices")
    public ResponseEntity<?> createDeviceForLane(@PathVariable Integer idCasello,
                                                 @PathVariable Integer numCorsia,
                                                 @RequestBody Map<String, Object> body) {
        try {
            String tipo  = (String) body.get("tipo");
            String stato = (String) body.getOrDefault("stato", body.getOrDefault("status", "ATTIVO"));
            var created = service.createForLane(idCasello, numCorsia, tipo, stato);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore creazione dispositivo"));
        }
    }

    // ------------------- MQTT: Richiesta Generazione Biglietto -------------------

    /**
     * Endpoint per il totem che richiede la generazione di un biglietto.
     * Il totem pubblica un evento sul broker MQTT che verrà ricevuto dalla telecamera.
     * 
     * POST /devices/totem/{idTotem}/generaBiglietto
     */
    @PostMapping("/devices/totem/{idTotem}/generaBiglietto")
    public ResponseEntity<?> triggerBiglietto(@PathVariable Integer idTotem) {
        try {
            // Verifica che il dispositivo sia un totem
            var dispositivo = service.getById(idTotem);
            if (dispositivo.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            var totem = dispositivo.get();
            if (!"TOTEM".equalsIgnoreCase(totem.getTipoDispositivo())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Il dispositivo non è un totem"));
            }

            // Crea l'evento di richiesta biglietto
            RichiestaBigliettoEvent evento = new RichiestaBigliettoEvent(
                    totem.getID(),
                    totem.getCorsia(),
                    totem.getCasello(),
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );

            // Pubblica l'evento sul broker MQTT
            mqttBroker.publish("totem/generaBiglietto", evento);

            return ResponseEntity.ok(Map.of(
                    "status", "ok",
                    "message", "Richiesta biglietto pubblicata dal totem sul broker MQTT",
                    "idTotem", idTotem,
                    "casello", totem.getCasello(),
                    "corsia", totem.getCorsia()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore pubblicazione richiesta: " + e.getMessage()));
        }
    }

    @PostMapping("/devices/telecamera/{idTelecamera}/generaBiglietto")
    public ResponseEntity<?> triggerTelepass(@PathVariable Integer idTelecamera) {
        try {

            var dispositivo = service.getById(idTelecamera);
            if (dispositivo.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            var telecamera = dispositivo.get();
            if (!"TELECAMERA".equalsIgnoreCase(telecamera.getTipoDispositivo())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Il dispositivo non è una telecamera"));
            }

            // Crea l'evento di richiesta biglietto
            RichiestaBigliettoEvent evento = new RichiestaBigliettoEvent(
                    telecamera.getID(),
                    telecamera.getCorsia(),
                    telecamera.getCasello(),
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );

            // Pubblica l'evento sul broker MQTT
            mqttBroker.publish("totem/generaBiglietto", evento);

            return ResponseEntity.ok(Map.of(
                    "status", "ok",
                    "message", "Richiesta biglietto pubblicata dalla telecamera sul broker MQTT",
                    "idTelecamera", idTelecamera,
                    "casello", telecamera.getCasello(),
                    "corsia", telecamera.getCorsia()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore pubblicazione richiesta: " + e.getMessage()));
        }
    }

    @PostMapping("/devices/totem/{idTotem}/{idBiglietto}/richiestaPagamento")
    public ResponseEntity<?> richiestaPagamento(@PathVariable Integer idTotem,
                                                @PathVariable Integer idBiglietto) {
        try {
            // Verifica che il dispositivo sia un totem
            var dispositivo = service.getById(idTotem);
            if (dispositivo.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            var totem = dispositivo.get();
            if (!"TOTEM".equalsIgnoreCase(totem.getTipoDispositivo())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Il dispositivo non è un totem"));
            }

            richiestaPagamentoEvent evento = new richiestaPagamentoEvent(idBiglietto, totem.getCasello(), totem.getCorsia());

            mqttBroker.publish("totem/pagaBiglietto", evento);

            return ResponseEntity.ok(Map.of(
                    "status", "ok",
                    "message", "Richiesta pagamento biglietto pubblicata dal totem sul broker MQTT",
                    "idTotem", idTotem,
                    "casello", totem.getCasello(),
                    "corsia", totem.getCorsia()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore pubblicazione richiesta: " + e.getMessage()));
        }
    }

    @PostMapping("/devices/totem/{idTelecamera}/{idBiglietto}/richiestaPagamento")
    public ResponseEntity<?> richiestaPagamentoTelepass(@PathVariable Integer idTelecamera,
                                                @PathVariable Integer idBiglietto) {
        try {
            var dispositivo = service.getById(idTelecamera);
            if (dispositivo.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            var telecamera = dispositivo.get();
            if (!"TELECAMERA".equalsIgnoreCase(telecamera.getTipoDispositivo())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Il dispositivo non è una telecamera"));
            }

            richiestaPagamentoEvent evento = new richiestaPagamentoEvent(idBiglietto, telecamera.getCasello(), telecamera.getCorsia());

            mqttBroker.publish("totem/pagaBiglietto", evento);

            return ResponseEntity.ok(Map.of(
                    "status", "ok",
                    "message", "Richiesta pagamento biglietto pubblicata dal totem sul broker MQTT",
                    "idTotem", idTelecamera,
                    "casello", telecamera.getCasello(),
                    "corsia", telecamera.getCorsia()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Errore pubblicazione richiesta: " + e.getMessage()));
        }
    }
}
