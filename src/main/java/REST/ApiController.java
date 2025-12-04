package REST;

import DB.*;
import DB.daoVeicoli;
import model.Autostrada.*;
import model.Dispositivi.Dispositivi;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.Locale;

@RestController
@RequestMapping("/api")
public class ApiController {

    // -------- LOGIN / LOGOUT / SESSIONE ----------

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username,
                                        @RequestParam String password,
                                        HttpSession session) {
        try {
            if (username == null || password == null) {
                return ResponseEntity.badRequest().body("{\"error\":\"Dati mancanti\"}");
            }

            String hashedFromDb = daoUtente.getHashedPassword(username);
            Boolean isAdminFromDb = daoUtente.isAdmin(username);

            if (hashedFromDb != null && BCrypt.checkpw(password, hashedFromDb)) {
                session.setAttribute("user", username);
                session.setAttribute("isAdmin", isAdminFromDb != null && isAdminFromDb);
                return ResponseEntity.ok("{\"status\":\"ok\"}");
            } else {
                return ResponseEntity.status(401).body("{\"error\":\"Credenziali errate\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno nel login\"}");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String username,
                                           @RequestParam String password,
                                           @RequestParam("confirmPassword") String confirm,
                                           @RequestParam String role,
                                           HttpSession session) {
        try {
            if (username == null || password == null || confirm == null) {
                return ResponseEntity.badRequest().body("{\"error\":\"Dati mancanti\"}");
            }
            if (!password.equals(confirm)) {
                return ResponseEntity.badRequest().body("{\"error\":\"Le password non coincidono\"}");
            }

            String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
            boolean isAdmin = "amministratore".equalsIgnoreCase(role);

            boolean ok = daoUtente.registrazione(username, hashed, isAdmin);
            if (ok) {
                session.setAttribute("user", username);
                session.setAttribute("isAdmin", isAdmin);
                return ResponseEntity.ok("{\"status\":\"ok\"}");
            } else {
                return ResponseEntity.status(409).body("{\"error\":\"Username gia esistente\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno\"}");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("{\"status\":\"ok\"}");
    }

    @GetMapping("/session")
    public ResponseEntity<String> sessionInfo(HttpSession session) {
        boolean loggedIn = session.getAttribute("user") != null;
        boolean isAdmin = false;
        if (loggedIn) {
            Boolean adminAttr = (Boolean) session.getAttribute("isAdmin");
            isAdmin = adminAttr != null && adminAttr;
        }
        String json = String.format(Locale.US, "{\"loggedIn\":%b,\"isAdmin\":%b}", loggedIn, isAdmin);
        return ResponseEntity.ok(json);
    }

    // -------- KPI TRAFFICO ----------

    @GetMapping("/traffic")
    public ResponseEntity<String> trafficKpi() {
        try {
            daoTraffico dao = new daoTraffico();
            Traffico kpi = dao.calcolaKpiTraffico();

            String json = String.format(Locale.US,
                    "{\"media\":%d,\"oggi\":%d,\"variazione\":%.1f}",
                    kpi.getMediaLast30Days(),
                    kpi.getTrafficToday(),
                    kpi.getPercentageChangeVsYesterday());

            return ResponseEntity.ok(json);
        } catch (Exception e) {
            System.err.println("ERRORE in /api/traffic:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno\"}");
        }
    }

    @GetMapping("/traffic/trend")
    public ResponseEntity<String> trafficTrend() {
        try {
            daoTraffico dao = new daoTraffico();
            String json = dao.getTrendUltimi30GiorniJson();
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            System.err.println("ERRORE in /api/traffic/trend:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno\"}");
        }
    }

    @GetMapping("/traffic/peaks")
    public ResponseEntity<String> trafficPeaks() {
        try {
            daoTraffico dao = new daoTraffico();
            String json = dao.getPicchiOrariOggiJson();
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            System.err.println("ERRORE in /api/traffic/peaks:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno\"}");
        }
    }

    // -------- ASSET ----------

    @GetMapping("/assets")
    public ResponseEntity<String> assets() {
        try {
            daoCasello caselloDao   = new daoCasello();
            daoCorsie corsiaDao     = new daoCorsie();
            daoDispositivi dispDao  = new daoDispositivi();

            int caselli     = caselloDao.contaCaselli();
            int corsie      = corsiaDao.contaCorsie();
            int dispositivi = dispDao.contaDispositivi();

            String json = String.format(Locale.US,
                    "{\"caselli\":%d,\"corsie\":%d,\"dispositivi\":%d}",
                    caselli, corsie, dispositivi);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            System.err.println("ERRORE in /api/assets:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno\"}");
        }
    }

    // -------- MULTE ----------

    @GetMapping("/fines")
    public ResponseEntity<String> finesCount() {
        try {
            daoMulte dao = new daoMulte();
            int count = dao.contaMulteUltime24h();
            String json = String.format(Locale.US, "{\"fines\":%d}", count);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            System.err.println("ERRORE in /api/fines:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno\"}");
        }
    }

    // -------- PAGAMENTI DA INCASSARE ----------

    @GetMapping("/payments")
    public ResponseEntity<String> payments() {
        try {
            daoPagamenti dao = new daoPagamenti();
            int count = dao.contaPagamentiDaIncassare();

            String json = String.format(Locale.US, "{\"pending\":%d}", count);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            System.err.println("ERRORE in /api/payments:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno\"}");
        }
    }


    @GetMapping("/fines/list")
    public ResponseEntity<String> finesList() {
        try {
            daoMulte dao = new daoMulte();
            String json = dao.getMulteRecentiJson();

            // rimuove le virgole prima di } o ] (trailing comma)
            json = json.replaceAll(",\\s*([}\\]])", "$1");

            return ResponseEntity.ok(json);
        } catch (Exception e) {
            System.err.println("ERRORE in /api/fines/list:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno\"}");
        }
    }


    // -------- AUTOSTRADE / CASELLI ----------

    @GetMapping("/tolls")
    public ResponseEntity<String> tolls() {
        try {
            daoAutostrada dao = new daoAutostrada();
            String json = dao.getRegioniAutostradeCaselli();
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            System.err.println("ERRORE in /api/tolls:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno\"}");
        }
    }
    // -------- RICERCA VEICOLI PER TARGA ----------

    @GetMapping("/vehicles")
    public ResponseEntity<String> searchVehicles(@RequestParam("plate") String plate) {
        if (plate == null || plate.isBlank()) {
            return ResponseEntity.badRequest().body("{\"error\":\"Targa mancante\"}");
        }

        try {
            daoVeicoli dao = new daoVeicoli(); // o il nome che usi (adatta sotto)
            String json = dao.getUltimiPassaggiPerTargaJson(plate.trim().toUpperCase());
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            System.err.println("ERRORE in /api/vehicles:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno\"}");
        }
    }

// ---------- AUTOSTRADE / CASELLI / CORSIE / DISPOSITIVI EXPLORER ----------

    // Elenco autostrade (per step "Autostrada")
    @GetMapping("/highways")
    public ResponseEntity<String> getHighways() {
        try {
            daoAutostrada dao = new daoAutostrada();
            // implementa questo metodo nel DAO per restituire un array JSON:
            // [ { "id_autostrada": 1, "nome_autostrada": "A4", "nome_regione": "Lombardia" }, ... ]
            String json = dao.getAutostradeJson();
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            System.err.println("ERRORE in GET /api/highways:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno\"}");
        }
    }

    // Caselli per una singola autostrada (step "Casello")
    @GetMapping("/highways/{idAutostrada}/tolls")
    public ResponseEntity<String> getTollsForHighway(@PathVariable("idAutostrada") int idAutostrada) {
        try {
            daoCasello dao = new daoCasello();
            String json = dao.getCaselliPerAutostrada(idAutostrada);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            System.err.println("ERRORE in GET /api/highways/" + idAutostrada + "/tolls:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno\"}");
        }
    }

    // Corsie per un casello (step "Corsia")
    @GetMapping("/tolls/{idCasello}/lanes")
    public ResponseEntity<String> getLanesForToll(@PathVariable("idCasello") int idCasello) {
        try {
            daoCorsie dao = new daoCorsie();
            // metodo da implementare: corsie per casello:
            // [ { "id_corsia": 5, "nome_corsia": "Corsia 1", "direzione": "Nord" }, ... ]
            String json = dao.getCorsiePerCaselloJson(idCasello);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            System.err.println("ERRORE in GET /api/tolls/" + idCasello + "/lanes:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno\"}");
        }
    }

    // Dispositivi per una corsia (step "Dispositivi")
    @GetMapping("/lanes/{idCorsia}/devices")
    public ResponseEntity<String> getDevicesForLane(@PathVariable("idCorsia") int idCorsia) {
        try {
            daoDispositivi dao = new daoDispositivi();
            // metodo da implementare: dispositivi per corsia:
            // [ { "id_dispositivo": 3, "tipo": "INGRESSO_MANUALE", "posizione": "Ingresso" }, ... ]
            String json = dao.getDispositiviPerCorsiaJson(idCorsia);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            System.err.println("ERRORE in GET /api/lanes/" + idCorsia + "/devices:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno\"}");
        }
    }

    // -------- REGIONI / AUTOSTRADE PER REGIONE ----------

    // Elenco regioni (step "Regione")
    @GetMapping("/regions")
    public ResponseEntity<String> getRegions() {
        try {
            daoAutostrada dao = new daoAutostrada();
            String json = dao.getRegioniJson(); // qui potrebbe esplodere
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            e.printStackTrace(); // importante: guarda lo stack trace in console
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }


    // Autostrade per una regione (step "Autostrada" dopo "Regione")
    @GetMapping("/regions/{idRegione}/highways")
    public ResponseEntity<String> getHighwaysForRegion(@PathVariable("idRegione") int idRegione) {
        try {
            daoAutostrada dao = new daoAutostrada();
            // Implementa nel DAO qualcosa come:
            // [ { "id_autostrada": 1, "nome_autostrada": "A4", "nome_regione": "Lombardia" }, ... ]
            String json = dao.getAutostradePerRegioneJson(idRegione);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            System.err.println("ERRORE in GET /api/regions/" + idRegione + "/highways:");
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Errore interno\"}");
        }
    }


    //------------------INSERT------------------//


    @GetMapping("/insert/highways/{autostrada}")
    public ResponseEntity<String> insertHighways(@PathVariable("autostrada") Autostrada a) {
        try {
            daoAutostrada dao = new daoAutostrada();
            dao.insertAutostrada(a);
            return ResponseEntity.status(HttpStatus.CREATED).body("Autostrada inserita correttamente");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'inserimento: " + e.getMessage());
        }
    }

    @GetMapping("/insert/ticket/{biglietto}")
    public ResponseEntity<String> insertTickets(@PathVariable("biglietto") Biglietto b) {
        try {
            daoBiglietto dao = new daoBiglietto();
            dao.insertBiglietto(b);
            return ResponseEntity.status(HttpStatus.CREATED).body("Biglietto inserito correttamente");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'inserimento: " + e.getMessage());
        }
    }

    @GetMapping("/insert/casello/{casello}")
    public ResponseEntity<String> insertCasello(@PathVariable("casello") Casello c) {
        try {
            daoCasello dao = new daoCasello();
            dao.insertCasello(c);
            return ResponseEntity.status(HttpStatus.CREATED).body("Casello inserito correttamente");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'inserimento: " + e.getMessage());
        }
    }

    @GetMapping("/insert/corsia/{corsia}")
    public ResponseEntity<String> insertCorsia(@PathVariable("corsia") Corsia c) {
        try {
            daoCorsie dao = new daoCorsie();
            dao.insertCorsia(c);
            return ResponseEntity.status(HttpStatus.CREATED).body("Corsia inserita correttamente");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'inserimento: " + e.getMessage());
        }
    }

    @GetMapping("/insert/dispositivi/{dispositivo}")
    public ResponseEntity<String> insertDispositivi(@PathVariable("dispositivo") Dispositivi d) {
        try {
            daoDispositivi dao = new daoDispositivi();
            dao.insertDispositivo(d);
            return ResponseEntity.status(HttpStatus.CREATED).body("Dispositivo inserito correttamente");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'inserimento: " + e.getMessage());
        }
    }


    @GetMapping("/insert/multa/{multa}")
    public ResponseEntity<String> insertMulta(@PathVariable("multa") Multa m) {
        try{
            daoMulte dao = new daoMulte();
            dao.insertMulta(m);
            return ResponseEntity.status(HttpStatus.CREATED).body("Multa inserita correttamente");
        }catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'inserimento: " + e.getMessage());
        }
    }

    @GetMapping("/insert/veicoli/{veicolo}")
    public ResponseEntity<String> insertVeicolo(@PathVariable("veicolo") Auto a) {
        try{
            daoVeicoli dao = new daoVeicoli();
            dao.insertVeicoli(a);
            return ResponseEntity.status(HttpStatus.CREATED).body("Veicolo inserito correttamente");
        }catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'inserimento: " + e.getMessage());
        }
    }

    @GetMapping("/insert/pagamento/{pagamento}")
    public ResponseEntity<String> insertPagamenti(@PathVariable("pagamento") Pagamento p) {
        try{
            daoPagamenti dao = new daoPagamenti();
            dao.insertPagamenti(p);
            return ResponseEntity.status(HttpStatus.CREATED).body("Pagamento inserito correttamente");
        }catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'inserimento: " + e.getMessage());
        }
    }

    //------------------ UPDATE ------------------//

    @GetMapping("/update/highways/{autostrada1}-{autostrada2}")
    public ResponseEntity<String> updateHighways(@PathVariable("autostrada1") Autostrada a1,
                                                 @PathVariable("autostrada2") Autostrada a2) {
        try {
            daoAutostrada dao = new daoAutostrada();
            return dao.aggiornaAutostrada(a1, a2);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'aggiornamento: " + e.getMessage());
        }
    }

    @GetMapping("update/ticket/{biglietto1}-{biglietto2}")
    public ResponseEntity<String> updateTicket(@PathVariable("biglietto1") Biglietto b1,
                                               @PathVariable("biglietto2") Biglietto b2){
        try {
            daoBiglietto dao = new daoBiglietto();
            return dao.aggiornaBiglietto(b1, b2);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'aggiornamento: " + e.getMessage());
        }
    }

    @GetMapping("update/casello/{casello1}-{casello2}")
    public ResponseEntity<String> updateCasello(@PathVariable("casello1") Casello c1,
                                               @PathVariable("casello2") Casello c2){
        try {
            daoCasello dao = new daoCasello();
            return dao.aggiornaCasello(c1, c2);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'aggiornamento: " + e.getMessage());
        }
    }

    @GetMapping("update/corsia/{corsia1}-{corsia2}")
    public ResponseEntity<String> updateCorsia(@PathVariable("corsia1") Corsia c1,
                                               @PathVariable("corsia2") Corsia c2){
        try {
            daoCorsie dao = new daoCorsie();
            return dao.aggiornaCorsia(c1, c2);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'aggiornamento: " + e.getMessage());
        }
    }

    @GetMapping("update/dispositivi/{dispositivo1}-{dispositivo2}")
    public ResponseEntity<String> updateDispositivi(@PathVariable("dispositivo1") Dispositivi d1,
                                                    @PathVariable("dispositivo2") Dispositivi d2){
        try {
            daoDispositivi dao = new daoDispositivi();
            return dao.updateDispositivo(d1, d2);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'aggiornamento: " + e.getMessage());
        }
    }

    @GetMapping("update/multe/{multa1}-{multa2}")
    public ResponseEntity<String> updateMulte(@PathVariable("multa1") Multa m1,
                                              @PathVariable("multa2") Multa m2){
        try {
            daoMulte dao = new daoMulte();
            return dao.updateMulta(m1, m2);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'aggiornamento: " + e.getMessage());
        }
    }

    @GetMapping("update/pagamenti/{pagamento1}-{pagamento2}")
    public ResponseEntity<String> updateMulte(@PathVariable("pagamento1") Pagamento p1,
                                              @PathVariable("pagamento2") Pagamento p2){
        try {
            daoPagamenti dao = new daoPagamenti();
            return dao.updatePagamento(p1, p2);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'aggiornamento: " + e.getMessage());
        }
    }

    //------------------ DELETE ------------------//

    @GetMapping("delete/autostrada/{autostrada}")
    public ResponseEntity<String> deleteHighways(@PathVariable("autostrada") Autostrada a) {
        try {
            daoAutostrada dao = new daoAutostrada();
            return dao.eliminaAutostrada(a);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'inserimento: " + e.getMessage());
        }
    }

    @GetMapping("delete/ticket/{biglietto1}")
    public ResponseEntity<String> deleteTicket(@PathVariable("biglietto1") Biglietto b1){
        try {
            daoBiglietto dao = new daoBiglietto();
            return dao.eliminaBiglietto(b1);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'aggiornamento: " + e.getMessage());
        }
    }

    @GetMapping("delete/casello/{casello1}")
    public ResponseEntity<String> deleteCasello(@PathVariable("casello1") Casello c1){
        try {
            daoCasello dao = new daoCasello();
            return dao.eliminaCasello(c1);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'aggiornamento: " + e.getMessage());
        }
    }

    @GetMapping("delete/corsia/{corsia1}")
    public ResponseEntity<String> deleteCorsia(@PathVariable("corsia1") Corsia c1){
        try {
            daoCorsie dao = new daoCorsie();
            return dao.eliminaCorsia(c1);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'aggiornamento: " + e.getMessage());
        }
    }

    @GetMapping("delete/dispositivi/{dispositivo1}")
    public ResponseEntity<String> deleteDispositivi(@PathVariable("dispositivo1") Dispositivi d1){
        try {
            daoDispositivi dao = new daoDispositivi();
            return dao.deleteDispositivo(d1);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'aggiornamento: " + e.getMessage());
        }
    }

    @GetMapping("delete/multe/{multa1}")
    public ResponseEntity<String> updateMulte(@PathVariable("multa1") Multa m1){
        try {
            daoMulte dao = new daoMulte();
            return dao.deleteMulta(m1);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'aggiornamento: " + e.getMessage());
        }
    }

    @GetMapping("delete/pagamenti/{pagamento1}")
    public ResponseEntity<String> updateMulte(@PathVariable("pagamento1") Pagamento p1){
        try {
            daoPagamenti dao = new daoPagamenti();
            return dao.deletePagamento(p1);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'aggiornamento: " + e.getMessage());
        }
    }
}
