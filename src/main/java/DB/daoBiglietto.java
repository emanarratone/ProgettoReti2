package DB;

import model.Autostrada.Autostrada;
import model.Autostrada.Biglietto;
import org.springframework.http.ResponseEntity;

import java.sql.*;

public class daoBiglietto {

    public void insertBiglietto(Biglietto biglietto) throws SQLException {
        String sql = "INSERT INTO BIGLIETTO (id_biglietto, matricola, targa_auto, classe_veicolo, timestamp_in, id_casello_in) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DbConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, biglietto.getID_biglietto());
            ps.setInt(2, biglietto.getID_Totem());
            ps.setString(3, biglietto.getAuto().getTarga());
            ps.setString(4, biglietto.getAuto().getTipoVeicolo().toString());
            ps.setTimestamp(5, Timestamp.valueOf(biglietto.getTimestamp_in()));
            ps.setInt(6, biglietto.getCasello_in().getIdCasello());

            ps.executeUpdate();
        }
    }

    public ResponseEntity<String> aggiornaBiglietto(Biglietto b1, Biglietto b2) throws SQLException {
        String sql = "UPDATE AUTOSTRADA SET id_biglietto, matricola, targa_auto, classe_veicolo, timestamp_in, id_casello_in WHERE id_biglietto=?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, b2.getID_biglietto());
            ps.setInt(2, b2.getID_Totem());
            ps.setString(3, b2.getAuto().getTarga());
            ps.setString(4, b2.getAuto().getTipoVeicolo().toString());
            ps.setTimestamp(5, Timestamp.valueOf(b2.getTimestamp_in()));
            ps.setInt(6, b2.getCasello_in().getIdCasello());
            ps.setInt(7, b1.getID_biglietto());
            ps.executeUpdate();
            if (ps.executeUpdate() > 0) {
                return ResponseEntity.ok("{\"message\":\"Biglietto aggiornato con successo\"}");
            } else {
                return ResponseEntity.status(404).body("{\"error\":\"Biglietto non trovato\"}");
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno durante l'aggiornamento\"}");
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
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno durante l'eliminazione\"}");
        }
    }

}
