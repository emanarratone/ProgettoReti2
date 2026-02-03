package REST;

import DB.daoUtente;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api")
public class ControllerUser {

    // ================= LOGIN / SESSIONE =================

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username,
                                        @RequestParam String password,
                                        HttpSession session) {
        try {
            if (username == null || username.isBlank() || password == null || password.isBlank()) {
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
            if (username == null || username.isBlank()
                    || password == null || password.isBlank()
                    || confirm == null || confirm.isBlank()) {
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
}
