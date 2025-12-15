package auto_service.controller;

import DB.daoVeicoli;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public class autostradaController {
    @GetMapping("/vehicles")
    public ResponseEntity<String> searchVehicles(@RequestParam("plate") String plate) {
        if (plate == null || plate.isBlank()) {
            return ResponseEntity.badRequest().body("{\"error\":\"Targa mancante\"}");
        }
        try {
            daoVeicoli dao = new daoVeicoli();
            String json = dao.getUltimiPassaggiPerTargaJson(plate.trim().toUpperCase());
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            System.err.println("ERRORE in /api/vehicles:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno\"}");
        }
    }
}
