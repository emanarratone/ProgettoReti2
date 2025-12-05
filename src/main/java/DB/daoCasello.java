package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import static DB.DbConnection.getConnection;

public class daoCasello {
    // INSERT casello + AUTOSTRADA_CONTIENE_CASELLO (POST /highways/{idAutostrada}/tolls)
    public void insertCasello(int idAutostrada, String nomeCasello, Integer limite, Double km) throws SQLException {
        String insertCasello = """
            INSERT INTO CASELLO (sigla, id_autostrada, is_closed, limite)
            VALUES (?, ?, FALSE, ?)
            RETURNING id_casello
            """;
        String insertLink = """
            INSERT INTO AUTOSTRADA_CONTIENE_CASELLO (id_autostrada, id_casello, progressiva_km)
            VALUES (?, ?, ?)
            """;

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            int idCasello;

            try (PreparedStatement ps = conn.prepareStatement(insertCasello)) {
                ps.setString(1, nomeCasello);
                ps.setInt(2, idAutostrada);
                ps.setInt(3, limite);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    idCasello = rs.getInt(1);
                }
            }

            try (PreparedStatement ps2 = conn.prepareStatement(insertLink)) {
                ps2.setInt(1, idAutostrada);
                ps2.setInt(2, idCasello);
                ps2.setDouble(3, km != null ? km : 0.0);
                ps2.executeUpdate();
            }

            conn.commit();
        }
    }
    // UPDATE casello + km (PUT /tolls/{idCasello})
    public void updateCasello(int idCasello, String nomeCasello, Double km) throws SQLException {
        String updCasello = "UPDATE CASELLO SET sigla = ? WHERE id_casello = ?";
        String updLink = "UPDATE AUTOSTRADA_CONTIENE_CASELLO SET progressiva_km = ? WHERE id_casello = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(updCasello)) {
                ps.setString(1, nomeCasello);
                ps.setInt(2, idCasello);
                ps.executeUpdate();
            }

            try (PreparedStatement ps2 = conn.prepareStatement(updLink)) {
                ps2.setDouble(1, km != null ? km : 0.0);
                ps2.setInt(2, idCasello);
                ps2.executeUpdate();
            }

            conn.commit();
        }
    }

    public int contaCaselli() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Casello";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }

    // GET /api/highways/{idAutostrada}/tolls
    // JSON: [ { "id_casello":1, "nome_casello":"Brescia Est", "km": 74.5 }, ... ]
    public String getCaselliPerAutostrada(int idAutostrada) throws SQLException {
        String sql = """
            SELECT c.id_casello,
                   c.sigla,
                   acc.progressiva_km AS km
            FROM CASELLO c
            JOIN AUTOSTRADA_CONTIENE_CASELLO acc
                ON c.id_casello = acc.id_casello
            WHERE acc.id_autostrada = ?
            ORDER BY acc.progressiva_km
            """;
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idAutostrada);
            try (ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;
                    int idCasello = rs.getInt("id_casello");
                    String sigla = rs.getString("sigla");
                    double km = rs.getDouble("km");
                    sb.append(String.format(Locale.US,
                            "{\"id_casello\":%d,\"nome_casello\":\"%s\",\"km\":%.2f}",
                            idCasello,
                            sigla.replace("\"", "\\\""),
                            km));
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }
    // DELETE casello (DELETE /tolls/{idCasello})
    public void deleteCasello(int idCasello) throws SQLException {
        String sql = "DELETE FROM CASELLO WHERE id_casello = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCasello);
            ps.executeUpdate();
        }
    }
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

}
