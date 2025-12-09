package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import static DB.DbConnection.getConnection;

public class daoCorsia {

    // INSERT corsia (POST /tolls/{idCasello}/lanes)
    public void insertCorsia(Integer idCasello, String verso, String tipo_corsia, Boolean is_closed) throws SQLException {
        String sql = """
            INSERT INTO CORSIA (num_corsia, id_casello, verso, tipo_corsia, is_closed)
            VALUES (?, ?, ?, ?, ?)
            """;
        // ricava num_corsia max+1 per quel casello
        int numCorsia = getNextNumCorsia(idCasello);

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, numCorsia);
            ps.setInt(2, idCasello);
            ps.setString(3, verso);
            ps.setString(4, tipo_corsia);
            ps.setBoolean(5, is_closed);
            ps.executeUpdate();
        }
    }

    private int getNextNumCorsia(Integer idCasello) throws SQLException {
        String sql = "SELECT COALESCE(MAX(num_corsia),0) + 1 AS next_num FROM CORSIA WHERE id_casello = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCasello);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("next_num");
            }
        }
    }
    //update corsia
    public void updateCorsia(Integer numCorsia,
                             Integer idCasello,
                             String verso,
                             String tipo,
                             boolean chiuso) throws SQLException {
        String sql = """
            UPDATE CORSIA
               SET verso = ?,
                   tipo_corsia = ?,
                   is_closed = ?
             WHERE num_corsia = ? AND id_casello = ?
            """;

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, verso);
            ps.setString(2, tipo);
            ps.setBoolean(3, chiuso);
            ps.setInt(4, numCorsia);
            ps.setInt(5, idCasello);
            ps.executeUpdate();
        }
    }


    // DELETE corsia
    public void deleteCorsia(Integer numCorsia,
                             Integer idCasello) throws SQLException {
        String sql = "DELETE FROM CORSIA WHERE num_corsia = ? AND id_casello = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, numCorsia);
            ps.setInt(2, idCasello);
            ps.executeUpdate();
        }
    }

    public  int contaCorsie() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Corsia";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }

    // GET /tolls/{idCasello}/lanes
    // JSON: [ { "id_corsia":1,"nome_corsia":"Corsia 1","direzione":"ENTRATA" }, ... ]
    public String getCorsiePerCasello(int idCasello) throws SQLException {
        String sql = """
        SELECT num_corsia, id_casello, verso, tipo_corsia, is_closed
        FROM CORSIA
        WHERE id_casello = ?
        ORDER BY num_corsia
    """;

        StringBuilder sb = new StringBuilder("[");
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idCasello);
            try (ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;

                    int numCorsia   = rs.getInt("num_corsia");
                    int idCas       = rs.getInt("id_casello");
                    String verso    = rs.getString("verso");
                    String tipo     = rs.getString("tipo_corsia");
                    boolean chiuso  = rs.getBoolean("is_closed");

                    sb.append(String.format(
                            Locale.US,
                            "{\"num_corsia\":%d," +
                                    "\"id_casello\":%d," +
                                    "\"verso\":\"%s\"," +
                                    "\"tipo_corsia\":\"%s\"," +
                                    "\"chiuso\":%s}",
                            numCorsia,
                            idCas,
                            verso.replace("\"", "\\\""),
                            tipo.replace("\"", "\\\""),
                            chiuso ? "true" : "false"
                    ));
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }

}