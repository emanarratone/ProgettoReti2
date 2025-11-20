package REST;

import DB.*;
import model.Personale.Utente;

import java.net.URL;
import java.nio.file.Paths;

import static DB.daoUtente.login;
import static spark.Spark.*;

public class ServerWeb {

    public static void main(String[] args) throws Exception {

        // opzionale: riduce i log
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");

        int httpsPort = 3000;
        port(httpsPort);

        // --- TLS: carica il keystore da src/main/resources/server-keystore.jks ---
        URL url = ServerWeb.class.getClassLoader().getResource("server-keystore.jks");
        if (url == null) {
            throw new RuntimeException("Keystore server-keystore.jks non trovato nel classpath");
        }

        // converte URL -> path di file locale (gestisce spazi, %20, ecc.)
        String keystoreFile = Paths.get(url.toURI()).toString();

        // password = quella usata in keytool: -storepass changeit -keypass changeit
        secure(keystoreFile, "changeit", null, null);

        // File statici: src/main/resources/html
        staticFiles.location("/html");

        // Home -> pagina di login
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

            Utente u;
            try {
                u = login(username, password);
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Errore interno nel login";
            }

            if (u != null) {
                // crea sessione e salva info utente
                req.session(true);
                req.session().attribute("user", u.getUser());
                req.session().attribute("isAdmin", u.isAdministrator());

                // redireziona in base al ruolo
                if (u.isAdministrator()) {
                    res.redirect("/dashboard_amministratore.html");
                } else {
                    res.redirect("/dashboard_impiegato.html");
                }
                return null;
            } else {
                // credenziali errate
                res.redirect("/login.html");
                return null;
            }
        });
// REGISTRAZIONE
        post("/register", (req, res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");
            String confirm  = req.queryParams("confirmPassword");
            String role     = req.queryParams("role"); // impiegato / amministratore

            if (username == null || password == null || confirm == null) {
                res.status(400);
                return "Dati mancanti";
            }
            if (!password.equals(confirm)) {
                res.status(400);
                return "Le password non coincidono";
            }

            boolean isAdmin = "amministratore".equalsIgnoreCase(role);

            try {
                boolean ok = daoUtente.registrazione(username, password, isAdmin);
                if (ok) {
                    // crea subito la sessione e manda alla dashboard
                    req.session(true);
                    req.session().attribute("user", username);
                    req.session().attribute("isAdmin", isAdmin);

                    if (isAdmin) {
                        res.redirect("/dashboard_amministratore.html");
                    } else {
                        res.redirect("/dashboard_impiegato.html");
                    }
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
            res.redirect("/login.html");
            return null;
        });

        // Esempio area protetta admin
        get("/area-admin", (req, res) -> {
            if (!isLoggedIn(req) || !isAdmin(req)) {
                res.redirect("/login.html");
                halt();
            }
            return "Area riservata amministratore";
        });

        // Esempio area protetta impiegato
        get("/area-impiegato", (req, res) -> {
            if (!isLoggedIn(req) || isAdmin(req)) {
                res.redirect("/login.html");
                halt();
            }
            return "Area riservata impiegato";
        });

        System.out.println("✓ Server HTTPS avviato su https://localhost:" + httpsPort);
    }

    // --------- metodi di utilità per la sessione ---------
    private static boolean isLoggedIn(spark.Request req) {
        return req.session(false) != null && req.session().attribute("user") != null;
    }

    private static boolean isAdmin(spark.Request req) {
        Boolean isAdmin = req.session().attribute("isAdmin");
        return isAdmin != null && isAdmin;
    }
}
