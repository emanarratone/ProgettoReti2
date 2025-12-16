package com.uniupo.dispositivi.controller;

import com.uniupo.dispositivi.service.DispositiviService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/lanes")
public class LaneDevicesController {

    private final DispositiviService service;

    public LaneDevicesController(DispositiviService service) {
        this.service = service;
    }

    @GetMapping("/{idCasello}/{numCorsia}/devices")
    public ResponseEntity<?> getDevicesForLane(@PathVariable Integer idCasello, @PathVariable Integer numCorsia) {
        return ResponseEntity.ok(service.getByCaselloAndCorsia(idCasello, numCorsia));
    }

    @PostMapping("/{idCasello}/{numCorsia}/devices")
    public ResponseEntity<?> createDeviceForLane(@PathVariable Integer idCasello, @PathVariable Integer numCorsia, @RequestBody Map<String,Object> body) {
        try {
            String tipo = (String) body.get("tipo");
            String stato = (String) body.getOrDefault("stato", body.getOrDefault("status", "ATTIVO"));
            var created = service.createForLane(idCasello, numCorsia, tipo, stato);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error","Errore creazione dispositivo"));
        }
    }
}