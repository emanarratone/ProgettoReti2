package DB;

import model.Personale.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class daoUtente {

    public static Utente findByUsername(String username) throws Exception {
        String sql = "SELECT username, password_hash, is_admin " +
                "FROM utenti " +
                "WHERE username = ?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String user     = rs.getString("username");
                    String passHash = rs.getString("password_hash");
                    boolean isAdmin = rs.getBoolean("is_admin");
                    return new Utente(user, passHash, isAdmin);
                } else {
                    return null;
                }
            }
        }
    }

    public static boolean registrazione(String username, String passwordHash, boolean isAdmin) throws Exception {
        // prima controlla se esiste già
        if (findByUsername(username) != null) {
            return false; // username già presente
        }

        String sql = "INSERT INTO utenti (username, password_hash, is_admin) VALUES (?, ?, ?)";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setBoolean(3, isAdmin);

            int n = ps.executeUpdate();
            return n == 1;
        }
    }

    public static String getHashedPassword(String username) throws SQLException {
        String query = "SELECT password_hash FROM utenti WHERE username = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash");
                } else {
                    return null;
                }
            }
        }
    }

    public static Boolean isAdmin(String username) throws SQLException {
        String query = "SELECT is_admin FROM utenti WHERE username = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_admin");
                } else {
                    return null;
                }
            }
        }
    }
}
