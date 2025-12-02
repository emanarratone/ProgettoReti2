package DB;

import model.Dispositivi.*;
import org.springframework.http.ResponseEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class daoDispositivi {

    public ResponseEntity<String> insertDispositivo(Dispositivi d) {
        String sqlDisp = "INSERT INTO Dispositivo (id_dispositivo, Stato, Num_corsia, sigla) VALUES (?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlDisp)) {

            ps.setInt(1, d.getID());
            ps.setString(2, d.getStatus());
            ps.setInt(3, d.getCorsia());
            ps.setString(4, getTipoDispositivo(d));

            int righeInserite = ps.executeUpdate();  // Corretto: executeUpdate() per INSERT
            if (righeInserite > 0) {
                return ResponseEntity.ok("{\"message\":\"Dispositivo inserito con successo\"}");
            } else {
                return ResponseEntity.internalServerError().body("{\"error\":\"Inserimento dispositivo fallito\"}");
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno durante l'inserimento\"}");
        }
    }


    public String  getTipoDispositivo(Dispositivi d) {
        if(d instanceof Sbarra) return "SBARRA";
        else if(d instanceof Telecamera) return "TELECAMERA";
        else return "TOTEM";
    }

    public ResponseEntity<String> updateDispositivo(int id, String nuovoStato) throws SQLException {
        String sql = "UPDATE Dispositivo SET Stato = ? WHERE id_dispositivo = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuovoStato);
            ps.setInt(2, id);
            int righeAggiornate = ps.executeUpdate();
            if (righeAggiornate > 0) {
                return ResponseEntity.ok("{\"message\":\"Aggiornamento avvenuto con successo\"}");
            } else {
                return ResponseEntity.status(404).body("{\"error\":\"Dispositivo non trovato\"}");
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno\"}");
        }
    }

    public ResponseEntity<String> deleteDispositivo(int id) {
        String sql = "DELETE FROM Dispositivo WHERE id_dispositivo = ?";

        try (Connection conn = DbConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, id);
                int righeEliminate= ps.executeUpdate();

                if (righeEliminate == 0) {
                    conn.rollback();
                    return ResponseEntity.status(404).body("{\"error\":\"Dispositivo non trovato\"}");
                }

                conn.commit();
                return ResponseEntity.ok("{\"message\":\"Dispositivo eliminato con successo\"}");
            } catch (SQLException ex) {
                conn.rollback();
                return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno durante l'eliminazione\"}");
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore di connessione al database\"}");
        }
    }


    public  int contaDispositivi() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Dispositivo";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }

    public String getDispositiviPerCorsiaJson(int idCorsia) throws SQLException {
        String sql =
                "SELECT id_dispositivo, stato, tipo_dispositivo " +
                        "FROM DISPOSITIVO " +
                        "WHERE id_corsia = ? " +
                        "ORDER BY id_dispositivo";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCorsia);

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                boolean first = true;

                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;

                    int id        = rs.getInt("id_dispositivo");
                    String stato  = rs.getString("stato");
                    String tipo   = rs.getString("tipo_dispositivo");

                    sb.append("{")
                            .append("\"id_dispositivo\":").append(id).append(",")
                            .append("\"tipo\":\"").append(tipo).append("\",")
                            .append("\"posizione\":\"").append(stato).append("\"")
                            .append("}");
                }

                sb.append("]");
                return sb.toString();
            }
        }
    }
}
