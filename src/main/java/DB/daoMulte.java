package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.postgresql.core.Oid.INTERVAL;

public class daoMulte {

    public int contaMulteUltime24h() throws SQLException {
        String sql = "SELECT COUNT(*) " +
                "FROM Multa " +
                "WHERE data >= NOW() - INTERVAL '24 hours' ";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }

    public String getMulteRecentiJson() throws SQLException {
        String SQL =
                "SELECT " +
                        "  id_multa, " +
                        "  targa, " +
                        "  importo, " +
                        "  data, " +
                        "  pagato, " +
                        "  motivo " +
                        "FROM multa " +
                        "WHERE data >= CURRENT_DATE - INTERVAL '7 days' " +
                        "ORDER BY data DESC, id_multa DESC";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder sb = new StringBuilder();
            sb.append("[");

            boolean first = true;
            while (rs.next()) {
                if (!first) sb.append(",");
                first = false;

                int id     = rs.getInt("id_multa");
                String targa = rs.getString("targa");
                double imp = rs.getDouble("importo");
                java.sql.Date data = rs.getDate("data");
                boolean pagato = rs.getBoolean("pagato");
                String motivo = rs.getString("motivo");

                // escape base per JSON
                targa  = targa.replace("\\", "\\\\").replace("\"", "\\\"");
                motivo = motivo.replace("\\", "\\\\").replace("\"", "\\\"");

                sb.append(String.format(java.util.Locale.US,
                        "{\"id_multa\":%d," +
                                "\"targa\":\"%s\"," +
                                "\"importo\":%.2f," +
                                "\"data\":\"%s\"," +
                                "\"pagato\":%s," +
                                "\"motivo\":\"%s\"}",
                        id, targa, imp, data.toString(),
                        pagato ? "true" : "false",
                        motivo));
            }
            sb.append("]");
            return sb.toString();
        }
    }
}
