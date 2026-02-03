package REST;

import DB.*;

import model.Autostrada.*;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Locale;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class ApiController {

    // ================= KPI / DASHBOARD  =================

    // LISTA TUTTI I CASELLI + AUTOSTRADA (per dashboard)
    @GetMapping("/tolls")
    public ResponseEntity<String> listAllTolls() {
        try {
            daoAutostrada dao = new daoAutostrada();
            // Implementa questo metodo nel DAO per restituire JSON tipo:
            // [ { "nome": "...", "nome_autostrada": "A4", ... }, ... ]
            String json = dao.getAutostradeJson();
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            System.err.println("ERRORE in GET /api/tolls:");
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Errore interno\"}");
        }
    }


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


    @GetMapping("/assets")
    public ResponseEntity<String> assets() {
        try {
            daoCasello caselloDao   = new daoCasello();
            daoCorsia corsiaDao     = new daoCorsia();
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
            json = json.replaceAll(",\\s*([}\\]])", "$1");
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            System.err.println("ERRORE in /api/fines/list:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno\"}");
        }
    }

    // ================= EXPLORER REGIONI / AUTOSTRADE / CASELLI / CORSIE / DISPOSITIVI =================

    // ---- REGIONI ----

    // usato dal JS -> GET /api/regions
    @GetMapping("/regions")
    public ResponseEntity<String> getRegions() {
        try {
            daoRegione dao = new daoRegione();
            String json = dao.getregioneJson();
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // POST /api/regions { "nomeRegione": "Lombardia" }
    @PostMapping("/regions")
    public ResponseEntity<String> createRegion(@RequestBody Map<String, Object> body) {
        try {
            String nome = (String) body.get("nomeRegione");
            if (nome == null || nome.isBlank()) {
                return ResponseEntity.badRequest()
                        .body("{\"error\":\"nomeRegione obbligatorio\"}");
            }
            Regione r = new Regione(nome);
            daoRegione dao = new daoRegione();
            dao.insertRegione(r);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("{\"status\":\"ok\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Errore creazione regione\"}");
        }
    }

    // PUT /api/regions/{idRegione}
    @PutMapping("/regions/{idRegione}")
    public ResponseEntity<String> updateRegion(@PathVariable int idRegione,
                                               @RequestBody Map<String, Object> body) {
        try {
            String nome = (String) body.get("nomeRegione");
            if (nome == null || nome.isBlank()) {
                return ResponseEntity.badRequest()
                        .body("{\"error\":\"nomeRegione obbligatorio\"}");
            }
            Regione r = new Regione(nome);
            daoRegione dao = new daoRegione();
            dao.updateRegione(idRegione, r);
            return ResponseEntity.ok("{\"status\":\"ok\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Errore aggiornamento regione\"}");
        }
    }

    // DELETE /api/regions/{idRegione}
    @DeleteMapping("/regions/{idRegione}")
    public ResponseEntity<String> deleteRegion(@PathVariable int idRegione) {
        try {
            daoRegione dao = new daoRegione();
            dao.deleteRegione(idRegione);
            return ResponseEntity.ok("{\"status\":\"ok\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Errore cancellazione regione\"}");
        }
    }

    // ---- AUTOSTRADE ----

    // GET /api/regions/{idRegione}/highways
    @GetMapping("/regions/{idRegione}/highways")
    public ResponseEntity<String> getHighwaysForRegion(@PathVariable int idRegione) {
        try {
            daoAutostrada dao = new daoAutostrada();
            String json = dao.getAutostradePerRegioneJson(idRegione);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Errore interno\"}");
        }
    }

        // POST /api/highways
        @PostMapping("/highways")
        public ResponseEntity<String> createHighway(@RequestBody Map<String, Object> body) {
            try {
                String sigla = (String) body.get("sigla");

                @SuppressWarnings("unchecked")
                List<Integer> idRegioni = (List<Integer>) body.get("idRegioni");

                if (sigla == null || sigla.isBlank() || idRegioni == null || idRegioni.isEmpty()) {
                    return ResponseEntity.badRequest()
                            .body("{\"error\":\"sigla e idRegioni obbligatori\"}");
                }

                daoAutostrada dao = new daoAutostrada();
                dao.insertAutostradePerRegioni(sigla, idRegioni);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("{\"status\":\"ok\"}");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError()
                        .body("{\"error\":\"Errore creazione autostrade\"}");
            }
        }



    // PUT /api/highways/{idAutostrada}
    @PutMapping("/highways/{idAutostrada}")
    public ResponseEntity<String> updateHighway(@PathVariable int idAutostrada,
                                                @RequestBody Map<String, Object> body) {
        try {
            String sigla = (String) body.get("citta");
            Number idRegioneNum = (Number) body.get("idRegione");
            Integer idRegione = idRegioneNum != null ? idRegioneNum.intValue() : null;

            if (sigla == null || sigla.isBlank()) {
                return ResponseEntity.badRequest()
                        .body("{\"error\":\"Nome autostrada mancante\"}");
            }
            if (idRegione == null) {
                return ResponseEntity.badRequest()
                        .body("{\"error\":\"Regione mancante\"}");
            }

            daoAutostrada dao = new daoAutostrada();
            dao.updateAutostrada(idAutostrada, sigla, idRegione);
            return ResponseEntity.ok("{\"status\":\"ok\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Errore aggiornamento autostrada\"}");
        }
    }

    // DELETE /api/highways/{idAutostrada}
    @DeleteMapping("/highways/{idAutostrada}")
    public ResponseEntity<String> deleteHighway(@PathVariable int idAutostrada) {
        try {
            daoAutostrada dao = new daoAutostrada();
            dao.deleteAutostrada(idAutostrada);
            return ResponseEntity.ok("{\"status\":\"ok\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Errore cancellazione autostrada\"}");
        }
    }

    // ---- CASELLI ----

    // GET /api/highways/{idAutostrada}/tolls
    @GetMapping("/highways/{idAutostrada}/tolls")
    public ResponseEntity<String> getTollsForHighway(@PathVariable int idAutostrada) {
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

    // POST /api/highways/{idAutostrada}/tolls { nome_casello, km }
    @PostMapping("/highways/{idAutostrada}/tolls")
    public ResponseEntity<String> createToll(@PathVariable int idAutostrada,
                                             @RequestBody Map<String, Object> body) {
        try {
            String nomeCasello = (String) body.get("nome_casello");
            Integer limite = (Integer) body.get("limite");

            if (nomeCasello == null || nomeCasello.isBlank()) {
                return ResponseEntity.badRequest()
                        .body("{\"error\":\"nome_casello obbligatorio\"}");
            }

            daoCasello dao = new daoCasello();
            dao.insertCasello(idAutostrada, nomeCasello, limite);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("{\"status\":\"ok\"}");
        } catch (Exception e) {
            System.err.println("ERRORE in POST /api/highways/" + idAutostrada + "/tolls:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore creazione casello\"}");
        }
    }

    // PUT /api/tolls/{idCasello}
    @PutMapping("/tolls/{idCasello}")
    public ResponseEntity<String> updateToll(@PathVariable int idCasello,
                                             @RequestBody Map<String, Object> body) {
        try {
            String nomeCasello = (String) body.get("nome_casello");
            Integer limite = (Integer) body.get("limite");
            Boolean chiuso = (Boolean) body.get("chiuso"); // <—

            if (nomeCasello == null || nomeCasello.isBlank()) {
                return ResponseEntity.badRequest()
                        .body("{\"error\":\"nome_casello obbligatorio\"}");
            }

            daoCasello dao = new daoCasello();
            dao.updateCasello(idCasello, nomeCasello, limite, chiuso != null && chiuso);
            return ResponseEntity.ok("{\"status\":\"ok\"}");
        } catch (Exception e) {
            System.err.println("ERRORE in PUT /api/tolls/" + idCasello + ":");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore aggiornamento casello\"}");
        }
    }

    // DELETE /api/tolls/{idCasello}
    @DeleteMapping("/tolls/{idCasello}")
    public ResponseEntity<String> deleteToll(@PathVariable int idCasello) {
        try {
            daoCasello dao = new daoCasello();
            dao.deleteCasello(idCasello);
            return ResponseEntity.ok("{\"status\":\"ok\"}");
        } catch (Exception e) {
            System.err.println("ERRORE in DELETE /api/tolls/" + idCasello + ":");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore eliminazione casello\"}");
        }
    }

    // ---- CORSIE ----

    // GET /api/tolls/{idCasello}/lanes
    @GetMapping("/tolls/{idCasello}/lanes")
    public ResponseEntity<String> getLanesForToll(@PathVariable int idCasello) {
        try {
            daoCorsia dao = new daoCorsia();
            String json = dao.getCorsiePerCasello(idCasello);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Errore interno\"}");
        }
    }

    // POST /api/tolls/{idCasello}/lanes { nome_corsia, direzione }
    @PostMapping("/tolls/{idCasello}/lanes")
    public ResponseEntity<String> createLane(@PathVariable int idCasello,
                                             @RequestBody Map<String, Object> body) {
        try {
            String verso = (String) body.get("verso");
            String tipo_corsia = (String) body.get("tipo_corsia");
            Boolean is_closed = (Boolean) body.get("chiuso");
            daoCorsia dao = new daoCorsia();
            dao.insertCorsia(idCasello, verso,tipo_corsia,is_closed);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("{\"status\":\"ok\"}");
        } catch (Exception e) {
            System.err.println("ERRORE in POST /api/tolls/" + idCasello + "/lanes:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore creazione corsia\"}");
        }
    }

    // PUT /api/lanes/{idCasello}/{numCorsia}
    @PutMapping("/lanes/{idCasello}/{numCorsia}")
    public ResponseEntity<String> updateLane(@PathVariable Integer idCasello,
                                             @PathVariable Integer numCorsia,
                                             @RequestBody Map<String, Object> body) {
        try {
            String verso = (String) body.get("verso");
            String tipo  = (String) body.get("tipo_corsia");
            Boolean chiuso = (Boolean) body.get("chiuso");

            if (verso == null || verso.isBlank() ||
                    tipo == null  || tipo.isBlank()) {
                return ResponseEntity.badRequest()
                        .body("{\"error\":\"verso e tipo_corsia obbligatori\"}");
            }

            daoCorsia dao = new daoCorsia();
            dao.updateCorsia(numCorsia, idCasello, verso, tipo, chiuso != null && chiuso);
            return ResponseEntity.ok("{\"status\":\"ok\"}");
        } catch (Exception e) {
            System.err.println("ERRORE in PUT /api/lanes/" + idCasello + "/" + numCorsia + ":");
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Errore aggiornamento corsia\"}");
        }
    }

    // DELETE /api/lanes/{idCasello}/{numCorsia}
    @DeleteMapping("/lanes/{idCasello}/{numCorsia}")
    public ResponseEntity<String> deleteLane(@PathVariable Integer idCasello,
                                             @PathVariable Integer numCorsia) {
        try {
            daoCorsia dao = new daoCorsia();
            dao.deleteCorsia(numCorsia, idCasello);
            return ResponseEntity.ok("{\"status\":\"ok\"}");
        } catch (Exception e) {
            System.err.println("ERRORE in DELETE /api/lanes/" + idCasello + "/" + numCorsia + ":");
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Errore eliminazione corsia\"}");
        }
    }

    // ---- DISPOSITIVI ----

    // GET /api/lanes/{idCasello}/{numCorsia}/devices
    @GetMapping("/lanes/{idCasello}/{numCorsia}/devices")
    public ResponseEntity<String> getDevices(@PathVariable int idCasello,
                                             @PathVariable int numCorsia) {
        try {
            daoDispositivi dao = new daoDispositivi();
            String json = dao.getDispositiviPerCorsiaJson(numCorsia, idCasello);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            System.err.println("ERRORE in GET /api/lanes/" + idCasello + "/" + numCorsia + "/devices:");
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Errore caricamento dispositivi\"}");
        }
    }


    // POST /api/lanes/{idCasello}/{numCorsia}/devices
    @PostMapping("/lanes/{idCasello}/{numCorsia}/devices")
    public ResponseEntity<String> createDevice(@PathVariable int idCasello,
                                               @PathVariable int numCorsia,
                                               @RequestBody Map<String, Object> body) {
        try {
            String tipo = (String) body.get("tipo");
            String stato = (String) body.get("stato"); // se vuoi usarlo

            if (tipo == null || tipo.isBlank()) {
                return ResponseEntity.badRequest()
                        .body("{\"error\":\"tipo obbligatorio\"}");
            }

            daoDispositivi dao = new daoDispositivi();
            dao.insertDispositivo(stato,numCorsia, tipo, idCasello);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("{\"status\":\"ok\"}");
        } catch (Exception e) {
            System.err.println("ERRORE in POST /api/lanes/" + idCasello + "/" + numCorsia + "/devices:");
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Errore creazione dispositivo\"}");
        }
    }


    // PUT /api/devices/{idDispositivo}
    @PutMapping("/devices/{idDispositivo}")
    public ResponseEntity<String> updateDevice(@PathVariable int idDispositivo,
                                               @RequestBody Map<String, Object> body) {
        try {
            String stato = (String) body.get("stato");

            if (stato == null || stato.isBlank()) {
                return ResponseEntity.badRequest()
                        .body("{\"error\":\"stato obbligatorio\"}");
            }

            daoDispositivi dao = new daoDispositivi();
            dao.updateDispositivo(idDispositivo, stato);
            return ResponseEntity.ok("{\"status\":\"ok\"}");
        } catch (Exception e) {
            System.err.println("ERRORE in PUT /api/devices/" + idDispositivo + ":");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore aggiornamento dispositivo\"}");
        }
    }

    // DELETE /api/devices/{idDispositivo}
    @DeleteMapping("/devices/{idDispositivo}")
    public ResponseEntity<String> deleteDevice(@PathVariable int idDispositivo) {
        try {
            daoDispositivi dao = new daoDispositivi();
            dao.deleteDispositivo(idDispositivo);
            return ResponseEntity.ok("{\"status\":\"ok\"}");
        } catch (Exception e) {
            System.err.println("ERRORE in DELETE /api/devices/" + idDispositivo + ":");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore eliminazione dispositivo\"}");
        }
    }

    // GET /api/regions/search?q=...
    @GetMapping("/regions/search")
    public ResponseEntity<String> searchRegions(@RequestParam("q") String query) {
        try {
            daoRegione dao = new daoRegione();
            String json = dao.searchRegioni(query);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Errore ricerca regioni\"}");
        }
    }
}