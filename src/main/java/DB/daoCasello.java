package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import static DB.DbConnection.getConnection;

public class daoCasello {
    // INSERT casello + AUTOSTRADA_CONTIENE_CASELLO (POST /highways/{idAutostrada}/tolls)
    public void insertCasello(int idAutostrada, String nomeCasello, Integer limite) throws SQLException {
        String insertCasello = """
            INSERT INTO CASELLO (sigla, id_autostrada, is_closed, limite)
            VALUES (?, ?, FALSE, ?)
            RETURNING id_casello
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
            conn.commit();
        }
    }
    // UPDATE casello + km (PUT /tolls/{idCasello})
    public void updateCasello(int idCasello, String nomeCasello, Integer limite, boolean is_closed) throws SQLException {
        String updCasello = "UPDATE CASELLO SET sigla = ?,limite = ?, is_closed = ? WHERE id_casello = ? ";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(updCasello)) {
                ps.setString(1, nomeCasello);
                ps.setInt(2, limite);
                ps.setBoolean(3, is_closed);
                ps.setInt(4, idCasello);
                ps.executeUpdate();
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
    public String getCaselliPerAutostrada(int idAutostrada) throws SQLException {
        String sql = """
        SELECT c.id_casello,
               c.sigla       AS nome_casello,
               c.limite,
               c.is_closed
        FROM CASELLO c
        WHERE c.id_autostrada = ?
        ORDER BY c.sigla;
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

                    int idCasello   = rs.getInt("id_casello");
                    String sigla    = rs.getString("nome_casello");
                    double limite   = rs.getDouble("limite");
                    boolean closed  = rs.getBoolean("is_closed");

                    sb.append(String.format(
                            Locale.US,
                            "{\"id_casello\":%d," +
                                    "\"nome_casello\":\"%s\"," +
                                    "\"limite\":%.0f," +
                                    "\"chiuso\":%s}",
                            idCasello,
                            sigla.replace("\"", "\\\""),
                            limite,
                            closed ? "true" : "false"
                    ));
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
