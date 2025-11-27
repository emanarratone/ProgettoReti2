package REST;

import DB.*;
import model.Autostrada.Traffico;
import model.Personale.Utente;

import java.net.URL;
import java.nio.file.Paths;

import static DB.daoUtente.*;   // oppure: import DB.daoUtente;
import static spark.Spark.*;

import org.mindrot.jbcrypt.BCrypt;

public class ServerWeb {

    public static void main(String[] args) throws Exception {

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");

        int httpsPort = 3000;
        port(httpsPort);

        // --- TLS ---
        URL url = ServerWeb.class.getClassLoader().getResource("server-keystore.jks");
        if (url == null) {
            throw new RuntimeException("Keystore server-keystore.jks non trovato nel classpath");
        }
        String keystoreFile = Paths.get(url.toURI()).toString();
        secure(keystoreFile, "changeit", null, null);

        // Static files
        staticFiles.location("/html");

        // Home -> login
        get("/", (req, res) -> {
            res.redirect("/login.html");
            return null;
        });

        // LOGIN
        post("/login", (req, res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");

            if (username == null || password == null) {
                res.redirect("/login.html");
                return null;
            }

            try {
                String hashedFromDb = daoUtente.getHashedPassword(username);
                Boolean isAdminFromDb = daoUtente.isAdmin(username);

                if (hashedFromDb != null && BCrypt.checkpw(password, hashedFromDb)) {
                    req.session(true);
                    req.session().attribute("user", username);
                    req.session().attribute("isAdmin", isAdminFromDb != null && isAdminFromDb);

                    res.redirect("/dashboard.html");
                    return null;
                } else {
                    res.redirect("/login.html");
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Errore interno nel login";
            }
        });

        // REGISTRAZIONE
        post("/register", (req, res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");
            String confirm  = req.queryParams("confirmPassword");
            String role     = req.queryParams("role");

            if (username == null || password == null || confirm == null) {
                res.status(400);
                return "Dati mancanti";
            }
            if (!password.equals(confirm)) {
                res.status(400);
                return "Le password non coincidono";
            }

            String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
            boolean isAdmin = "amministratore".equalsIgnoreCase(role);

            try {
                boolean ok = daoUtente.registrazione(username, hashed, isAdmin);
                if (ok) {
                    req.session(true);
                    req.session().attribute("user", username);
                    req.session().attribute("isAdmin", isAdmin);

                    res.redirect("/index.html");
                    return null;
                } else {
                    res.status(409);
                    return "Username già esistente";
                }
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Errore interno";
            }
        });

        // LOGOUT
        post("/logout", (req, res) -> {
            if (req.session(false) != null) {
                req.session().invalidate();
            }
            res.redirect("/index.html");
            return null;
        });

        // Area protetta admin
        get("/dash", (req, res) -> {
            if (!isLoggedIn(req) || !isAdmin(req)) {
                res.redirect("/login.html");
                halt();
            }
            return "Area riservata amministratore";
        });

        // Stato sessione
        get("/api/session", (req, res) -> {
            boolean loggedIn = req.session(false) != null && req.session().attribute("user") != null;
            boolean isAdmin = false;

            if (loggedIn) {
                Boolean adminAttr = req.session().attribute("isAdmin");
                isAdmin = adminAttr != null && adminAttr;
            }

            res.type("application/json");
            return String.format("{\"loggedIn\":%b,\"isAdmin\":%b}", loggedIn, isAdmin);
        });

        // KPI TRAFFICO (già esistente)
        get("/api/traffic", (req, res) -> {
            try {
                System.out.println("Ricevuta richiesta /api/traffic");
                TrafficoDao dao = new TrafficoDao();
                Traffico kpi = dao.calcolaKpiTraffico();
                System.out.println("KPI calcolati: " + kpi);

                res.type("application/json");
                return String.format(java.util.Locale.US,
                        "{\"media\":%d,\"oggi\":%d,\"variazione\":%.1f}",
                        kpi.getMediaLast30Days(),
                        kpi.getTrafficToday(),
                        kpi.getPercentageChangeVsYesterday());

            } catch (Exception e) {
                System.err.println("ERRORE in /api/traffic:");
                e.printStackTrace();
                res.status(500);
                return "{\"error\":\"Errore interno\"}";
            }
        });

        // =========================
        // NUOVE API PER I KPI
        // =========================

        // Numero di caselli / corsie / dispositivi
        get("/api/assets", (req, res) -> {
            try {
                int caselli     = daoCasello.contaCaselli();
                int corsie      = daoCorsie.contaCorsie();
                int dispositivi = daoDispositivi.contaDispositivi();

                res.type("application/json");

                return String.format(
                        "{\"caselli\":%d,\"corsie\":%d,\"dispositivi\":%d}",
                        caselli, corsie, dispositivi
                );
            } catch (Exception e) {
                System.err.println("ERRORE in /api/assets:");
                e.printStackTrace();
                res.status(500);
                return "{\"error\":\"Errore interno\"}";
            }
        });

        // Multe giornaliere (ultime 24h)
        get("/api/fines", (req, res) -> {
            try {
                int count = daoMulte.contaMulteUltime24h();

                res.type("application/json");
                // kpiFines <- fines
                return String.format("{\"fines\":%d}", count);
            } catch (Exception e) {
                System.err.println("ERRORE in /api/fines:");
                e.printStackTrace();
                res.status(500);
                return "{\"error\":\"Errore interno\"}";
            }
        });

        // Pagamenti da incassare
        get("/api/payments", (req, res) -> {
            try {
                PagamentiDao dao = new PagamentiDao();
                int count = dao.contaPagamentiDaIncassare();

                res.type("application/json");
                // kpiPayments <- pending
                return String.format("{\"pending\":%d}", count);
            } catch (Exception e) {
                System.err.println("ERRORE in /api/payments:");
                e.printStackTrace();
                res.status(500);
                return "{\"error\":\"Errore interno\"}";
            }
        });

        System.out.println("✓ Server HTTPS avviato su https://localhost:" + httpsPort);
    }

    // --------- utilità sessione ---------
    private static boolean isLoggedIn(spark.Request req) {
        return req.session(false) != null && req.session().attribute("user") != null;
    }

    private static boolean isAdmin(spark.Request req) {
        Boolean isAdmin = req.session().attribute("isAdmin");
        return isAdmin != null && isAdmin;
    }
}
