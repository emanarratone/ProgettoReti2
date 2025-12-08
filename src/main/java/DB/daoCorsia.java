package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import static DB.DbConnection.getConnection;

public class daoCorsia {

    // INSERT corsia (POST /tolls/{idCasello}/lanes)
    public void insertCorsia(Integer idCasello, String direzione) throws SQLException {
        String sql = """
            INSERT INTO CORSIA (num_corsia, id_casello, verso, tipo_corsia, is_closed)
            VALUES (?, ?, ?, 'MANUALE', FALSE)
            """;
        // ricava num_corsia max+1 per quel casello
        int nextNum = getNextNumCorsia(idCasello);

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, nextNum);
            ps.setInt(2, idCasello);
            ps.setString(3, direzione);
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
    public void updateCorsia(Integer numCorsia, String nomeCorsia, String direzione) throws SQLException {
        String sql = "UPDATE CORSIA SET verso = ? WHERE num_corsia = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, direzione != null && !direzione.isBlank() ? direzione : "ENTRATA");
            ps.setInt(2, numCorsia);
            ps.executeUpdate();
        }
    }

    // DELETE corsia
    public void deleteCorsia(Integer numCorsia, Integer idCasello) throws SQLException {
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
    public String getCorsiePerCaselloJson(int idCasello) throws SQLException {
        String sql = """
            SELECT num_corsia,
                   verso,
                   tipo_corsia,
                   is_closed
            FROM CORSIA
            WHERE id_casello = ?
            ORDER BY num_corsia
            """;
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCasello);
            try (ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;
                    int num = rs.getInt("num_corsia");
                    String verso = rs.getString("verso");
                    String tipo = rs.getString("tipo_corsia");
                    boolean closed = rs.getBoolean("is_closed");

                    String nomeCorsia = "Corsia " + num;
                    sb.append(String.format(Locale.US,
                            "{\"id_corsia\":%d,\"nome_corsia\":\"%s\",\"direzione\":\"%s\",\"tipo\":\"%s\",\"closed\":%b}",
                            num,
                            nomeCorsia,
                            verso,
                            tipo,
                            closed));
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }



}
