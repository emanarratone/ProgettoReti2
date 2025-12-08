package DB;

import model.Autostrada.Auto;
import model.Autostrada.Autostrada;
import model.Autostrada.Biglietto;
import org.springframework.http.ResponseEntity;

import java.sql.*;

public class daoBiglietto {

    public ResponseEntity<String> insertBiglietto(Biglietto biglietto) throws SQLException {
        String sql = "INSERT INTO BIGLIETTO (matricola, targa, timestamp_in, casello_in) VALUES (?, ?, ?, ?)";

        try (Connection con = DbConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, biglietto.getID_Totem());
            ps.setString(2, biglietto.getAuto());
            ps.setTimestamp(3, Timestamp.valueOf(biglietto.getTimestamp_in()));
            ps.setInt(4, biglietto.getCasello_in());

            ps.executeUpdate();
            if (ps.executeUpdate() > 0) {
                return ResponseEntity.ok("{\"message\":\"Biglietto aggiornato con successo\"}");
            } else {
                return ResponseEntity.status(404).body("{\"error\":\"Biglietto non trovato\"}");
            }
        }
    }

    public ResponseEntity<String> aggiornaBiglietto(Biglietto b1, Biglietto b2) throws SQLException {
        String sql = "UPDATE BIGLIETTO SET matricola=?, targa=?, timestamp_in=?, casello_in=? WHERE id_biglietto=?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, b2.getID_Totem());
            ps.setString(2, b2.getAuto());
            ps.setTimestamp(3, Timestamp.valueOf(b2.getTimestamp_in()));
            ps.setInt(4, b2.getCasello_in());
            ps.setInt(5, b1.getID_biglietto());
            ps.executeUpdate();
            if (ps.executeUpdate() > 0) {
                return ResponseEntity.ok("{\"message\":\"Biglietto aggiornato con successo\"}");
            } else {
                return ResponseEntity.status(404).body("{\"error\":\"Biglietto non trovato\"}");
            }
        }
    }

    public ResponseEntity<String> eliminaBiglietto(Biglietto b) throws SQLException {
        String sql = "DELETE FROM Biglietto WHERE id_biglietto = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, b.getID_biglietto());
            ps.executeUpdate();
            if (ps.executeUpdate() > 0) {
                return ResponseEntity.ok("{\"message\":\"Biglietto eliminato con successo\"}");
            } else {
                return ResponseEntity.status(404).body("{\"error\":\"Biglietto non trovato\"}");
            }
        }
    }

}
