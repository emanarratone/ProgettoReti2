package DB;

import model.Autostrada.Auto;
import org.springframework.http.ResponseEntity;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class daoVeicoli {

    public ResponseEntity<String> insertVeicoli(Auto a) throws SQLException {
        String s = "INSERT INTO Auto (targa, classe_veicolo) VALUES (?,?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(s)) {

            ps.setString(1, a.getTarga());
            ps.setString(2, a.getTipoVeicolo().toString());

            int righeInserite = ps.executeUpdate(); // INSERT usa executeUpdate()[web:3][web:17]

            if (righeInserite > 0) {
                return ResponseEntity.ok("{\"message\":\"Veicolo inserito con successo\"}");
            } else {
                return ResponseEntity.internalServerError().body("{\"error\":\"Inserimento veicolo fallito\"}");
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno durante l'inserimento\"}");
        }
    }

    public String getUltimiPassaggiPerTargaJson(String targa) throws SQLException {
        String sql =
                "SELECT " +
                        "  b.timestamp_in, " +
                        "  c_in.sigla  AS casello_ingresso, " +
                        "  p.timestamp_out, " +
                        "  c_out.sigla AS casello_uscita, " +
                        "  p.importo " +
                        "FROM BIGLIETTO b " +
                        "JOIN PAGAMENTO p   ON p.id_biglietto = b.id_biglietto " +
                        "JOIN CASELLO   c_in  ON c_in.id_casello  = b.casello_in " +
                        "JOIN CASELLO   c_out ON c_out.id_casello = p.casello_out " +
                        "WHERE b.targa = ? " +
                        "ORDER BY b.timestamp_in DESC " +
                        "LIMIT 20";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, targa);
            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");

                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        .withLocale(Locale.ITALY);

                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;

                    Timestamp tin  = rs.getTimestamp("timestamp_in");
                    Timestamp tout = rs.getTimestamp("timestamp_out");
                    String cin     = rs.getString("casello_ingresso");
                    String cout    = rs.getString("casello_uscita");
                    double importo = rs.getDouble("importo");

                    sb.append("{")
                            .append("\"timestampIn\":\"").append(
                                    tin != null ? tin.toLocalDateTime().format(fmt) : ""
                            ).append("\",")
                            .append("\"caselloIn\":\"").append(cin != null ? cin : "").append("\",")
                            .append("\"timestampOut\":\"").append(
                                    tout != null ? tout.toLocalDateTime().format(fmt) : ""
                            ).append("\",")
                            .append("\"caselloOut\":\"").append(cout != null ? cout : "").append("\",")
                            .append("\"importo\":").append(String.format(Locale.US, "%.2f", importo))
                            .append("}");
                }

                sb.append("]");
                return sb.toString();
            }
        }
    }


    public ResponseEntity<String> deleteVeicolo(String targa) {
        String sql = "DELETE FROM Auto WHERE Targa = ?";

        try (Connection conn = DbConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, targa);
                int righeEliminate = ps.executeUpdate();

                if (righeEliminate == 0) {
                    conn.rollback();
                    return ResponseEntity.status(404).body("{\"error\":\"Auto non trovata\"}");
                }

                return ResponseEntity.ok("{\"message\":\"Auto eliminata con successo\"}");

            } catch (SQLException ex) {
                return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno durante l'eliminazione\"}");
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore di connessione al database\"}");
        }
    }
}
