package DB;

import model.Autostrada.Corsia;
import org.springframework.http.ResponseEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class daoCorsie {

    public void insertCorsia(Corsia c) throws SQLException {
        String sql = "INSERT INTO Corsia (id_corsia, id_casello, verso, tipo, isClosed) VALUES (?,?,?,?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getID());
            ps.setString(2, c.getCasello());
            ps.setString(3, c.getVerso().toString());
            ps.setString(4, c.getTipo().toString());
            ps.setBoolean(5, c.getClosed());
            ps.executeUpdate();
        }
    }

    public ResponseEntity<String> aggiornaCorsia(Corsia c1, Corsia c2) throws SQLException {
        String sql = "UPDATE Corsia SET id_corsia=?, id_casello=?, verso=?, tipo=?, isClosed=? WHERE id_corsia = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c2.getID());
            ps.setString(2, c2.getCasello());
            ps.setString(3, c2.getVerso().toString());
            ps.setString(4, c2.getTipo().toString());
            ps.setBoolean(5, c2.getClosed());
            ps.setInt(6, c1.getID());
            ps.executeUpdate();
            if (ps.executeUpdate() > 0) {
                return ResponseEntity.ok("{\"message\":\"Corsia aggiornata con successo\"}");
            } else {
                return ResponseEntity.status(404).body("{\"error\":\"Corsia non trovata\"}");
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno durante l'aggiornamento\"}");
        }
    }

    public ResponseEntity<String> eliminaCorsia(Corsia c) throws SQLException {
        String sql = "DELETE FROM Corsia WHERE id_corsia = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getID());
            ps.executeUpdate();
            if (ps.executeUpdate() > 0) {
                return ResponseEntity.ok("{\"message\":\"Corsia eliminata con successo\"}");
            } else {
                return ResponseEntity.status(404).body("{\"error\":\"Autostrada non trovata\"}");
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno durante l'eliminazione\"}");
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

    public String getCorsiePerCaselloJson(int idCasello) throws SQLException {
        String sql =
                "SELECT id_corsia, verso, numero_corsia, tipo_corsia " +
                        "FROM CORSIA " +
                        "WHERE id_casello = ? " +
                        "ORDER BY numero_corsia";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCasello);

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                boolean first = true;

                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;

                    int id       = rs.getInt("id_corsia");
                    String verso = rs.getString("verso");
                    int num      = rs.getInt("numero_corsia");
                    String tipo  = rs.getString("tipo_corsia");

                    sb.append("{")
                            .append("\"id_corsia\":").append(id).append(",")
                            .append("\"nome_corsia\":\"Corsia ").append(num).append("\",")
                            .append("\"direzione\":\"").append(verso).append("\",")
                            .append("\"tipo_corsia\":\"").append(tipo).append("\"")
                            .append("}");
                }

                sb.append("]");
                return sb.toString();
            }
        }
    }

}
