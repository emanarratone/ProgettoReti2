package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class daoCasello {
    public void inserisciCasello(String sigla) throws SQLException {
        String sql = "INSERT INTO Casello (Sigla) VALUES (?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sigla);
            ps.executeUpdate();
        }
    }

    public void aggiornaCasello(String vecchiaSigla, String nuovaSigla) throws SQLException {
        String sql = "UPDATE Casello SET Sigla = ? WHERE Sigla = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuovaSigla);
            ps.setString(2, vecchiaSigla);
            ps.executeUpdate();
        }
    }

    public void eliminaCasello(String sigla) throws SQLException {
        String sql = "DELETE FROM Casello WHERE Sigla = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sigla);
            ps.executeUpdate();
        }
    }

    public int contaCaselli() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Casello";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }
}
