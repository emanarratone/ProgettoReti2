package DB;

import model.Personale.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class daoUtente {

    public static Utente login(String username, String password) throws Exception {
        String sql = "SELECT username, password_hash, is_admin " +
                "FROM utenti " +
                "WHERE username = ? AND password_hash = ?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password); // per il progetto va bene in chiaro

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String user = rs.getString("username");
                    String pass = rs.getString("password_hash");
                    boolean isAdmin = rs.getBoolean("is_admin");
                    return new Utente(user, pass, isAdmin);
                } else {
                    return null; // credenziali errate
                }
            }
        }
    }

    public static boolean registrazione(String username, String password, boolean isAdmin) throws Exception {
        String sql = "INSERT INTO utenti (username, password_hash, is_admin) VALUES (?, ?, ?)";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);      // per il progetto va bene in chiaro
            ps.setBoolean(3, isAdmin);

            int n = ps.executeUpdate();
            return n == 1;
        } catch (org.postgresql.util.PSQLException e) {
            // username duplicato (vincolo UNIQUE)
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate")) {
                return false;
            }
            throw e;
        }
    }

    public static String getHashedPassword(String username) throws SQLException {
        String query = "SELECT password_hash FROM utenti WHERE username = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash"); // qui si recupera l'hash salvato
                } else {
                    return null; // utente non trovato
                }
            }
        }
    }

    // Metodo per verificare se un utente è amministratore
    public static Boolean isAdmin(String username) throws SQLException {
        String query = "SELECT is_admin FROM utenti WHERE username = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_admin");
                } else {
                    return null; // utente non trovato
                }
            }
        }
    }


}
