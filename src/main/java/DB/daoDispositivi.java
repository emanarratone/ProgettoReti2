package DB;

import model.Dispositivi.*;
import org.springframework.http.ResponseEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class daoDispositivi {

    public ResponseEntity<String> insertDispositivo(Dispositivi d) throws SQLException{
        String sqlDisp = "INSERT INTO Dispositivo (id_dispositivo, Stato, corsia, casello, tipo_dispositivo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlDisp)) {

            ps.setInt(1, d.getID());
            ps.setString(2, d.getStatus());
            ps.setInt(3, d.getCorsia().getNumCorsia());
            ps.setInt(4, d.getCasello().getId());
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

    public ResponseEntity<String> updateDispositivo(Dispositivi d1, Dispositivi d2) throws SQLException {
        String sql = "UPDATE Dispositivo SET id_dispositivo=?, Stato=?, corsia=?, tipo=? WHERE id_dispositivo = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, d2.getID());
            ps.setString(2, d2.getStatus());
            ps.setInt(3, d2.getCorsia().getNumCorsia());
            ps.setString(4, getTipoDispositivo(d2));
            ps.setInt(5, d1.getID());
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

    public ResponseEntity<String> deleteDispositivo(Dispositivi d) throws SQLException {
        String sql = "DELETE FROM Dispositivo WHERE id_dispositivo = ?";

        try (Connection conn = DbConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, d.getID());
                int righeEliminate= ps.executeUpdate();

                if (righeEliminate == 0) {
                    return ResponseEntity.status(404).body("{\"error\":\"Dispositivo non trovato\"}");
                }

                conn.commit();
                return ResponseEntity.ok("{\"message\":\"Dispositivo eliminato con successo\"}");
            } catch (SQLException ex) {
                return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno durante l'eliminazione\"}");
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
                "SELECT id_dispositivo, stato " +
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
