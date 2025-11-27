package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class daoCorsie {

    public void inserisciCorsia(int numCorsia, String verso) throws SQLException {
        String sql = "INSERT INTO Corsia (Num_corsia, verso) VALUES (?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numCorsia);
            ps.setString(2, verso);
            ps.executeUpdate();
        }
    }

    public void aggiornaCorsia(int numCorsia, String nuovoVerso) throws SQLException {
        String sql = "UPDATE Corsia SET verso = ? WHERE Num_corsia = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuovoVerso);
            ps.setInt(2, numCorsia);
            ps.executeUpdate();
        }
    }

    public void eliminaCorsia(int numCorsia) throws SQLException {
        String sql = "DELETE FROM Corsia WHERE Num_corsia = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numCorsia);
            ps.executeUpdate();
        }
    }
    
    public  int contaCorsie() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Corsia";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }
}
