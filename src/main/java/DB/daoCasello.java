package DB;

import model.Autostrada.Casello;
import org.springframework.http.ResponseEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class daoCasello {
    public void insertCasello(Casello c) throws SQLException {
        String sql = "INSERT INTO Casello (id_casello, Sigla, autostrada, isClosed, limite) VALUES (?,?,?,?,?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getId());
            ps.setString(2, c.getSigla());
            ps.setInt(3, c.getAutostrada());
            ps.setBoolean(4, c.getClosed());
            ps.setInt(5, c.getLimite());
            ps.executeUpdate();
        }
    }

    public ResponseEntity<String> aggiornaCasello(Casello c1, Casello c2) throws SQLException {
        String sql = "UPDATE Casello SET id_casello=?, Sigla=?, autostrada=?, isClosed=?, limite=? WHERE Sigla = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c2.getId());
            ps.setString(2, c2.getSigla());
            ps.setInt(3, c2.getAutostrada());
            ps.setBoolean(4, c2.getClosed());
            ps.setInt(5, c2.getLimite());
            ps.setInt(6, c1.getId());
            ps.executeUpdate();
            if (ps.executeUpdate() > 0) {
                return ResponseEntity.ok("{\"message\":\"Casello aggiornato con successo\"}");
            } else {
                return ResponseEntity.status(404).body("{\"error\":\"Casello non trovato\"}");
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno durante l'aggiornamento\"}");
        }
    }

    public ResponseEntity<String> eliminaCasello(Casello c) throws SQLException {
        String sql = "DELETE FROM Casello WHERE Sigla = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getId());
            ps.executeUpdate();
            if (ps.executeUpdate() > 0) {
                return ResponseEntity.ok("{\"message\":\"Casello eliminato con successo\"}");
            } else {
                return ResponseEntity.status(404).body("{\"error\":\"Casello non trovato\"}");
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno durante l'eliminazione\"}");
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

    public String getCaselliPerAutostrada(int idAutostrada) throws SQLException {
        String sql =
                "SELECT " +
                        "  c.id_casello, " +
                        "  c.sigla AS nome_casello, " +
                        "  acc.progressiva_km AS km " +
                        "FROM CASELLO c " +
                        "LEFT JOIN AUTOSTRADA_CONTIENE_CASELLO acc " +
                        "  ON acc.id_casello = c.id_casello " +
                        "WHERE c.id_autostrada = ? " +
                        "ORDER BY acc.progressiva_km NULLS FIRST, c.sigla";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAutostrada);

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");

                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;

                    int idCasello        = rs.getInt("id_casello");
                    String nomeCasello   = rs.getString("nome_casello");
                    double km            = rs.getDouble("km");
                    boolean kmWasNull    = rs.wasNull();

                    if (nomeCasello == null) nomeCasello = "";

                    sb.append("{")
                            .append("\"id_casello\":").append(idCasello).append(",")
                            .append("\"nome_casello\":\"").append(escapeJson(nomeCasello)).append("\",");

                    if (kmWasNull) {
                        sb.append("\"km\":null");
                    } else {
                        sb.append("\"km\":").append(String.format(java.util.Locale.US, "%.2f", km));
                    }

                    sb.append("}");
                }

                sb.append("]");
                return sb.toString();
            }
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

}
