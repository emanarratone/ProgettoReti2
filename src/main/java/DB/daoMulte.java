package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


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
                        "    m.id_multa, " +
                        "    m.targa, " +
                        "    m.importo, " +
                        "    m.data, " +
                        "    m.pagato, " +
                        "    r.nome AS nome_regione " +
                        "FROM MULTA m " +
                        "LEFT JOIN BIGLIETTO b ON b.id_biglietto = m.id_biglietto " +
                        "LEFT JOIN CASELLO c   ON c.id_casello   = b.casello_in " +
                        "LEFT JOIN AUTOSTRADA a ON a.id_autostrada = c.id_autostrada " +
                        "LEFT JOIN REGIONE r    ON r.id_regione   = a.regione";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder sb = new StringBuilder();
            sb.append("[");

            boolean first = true;
            while (rs.next()) {
                if (!first) sb.append(",");
                first = false;

                int id              = rs.getInt("id_multa");
                String targa        = rs.getString("targa");
                double imp          = rs.getDouble("importo");
                java.sql.Date data  = rs.getDate("data");
                boolean pagato      = rs.getBoolean("pagato");
                String nomeRegione  = rs.getString("nome_regione");

                if (targa == null) targa = "";
                if (nomeRegione == null) nomeRegione = "";

                // escape base per stringhe JSON
                targa       = targa.replace("\\", "\\\\").replace("\"", "\\\"");
                nomeRegione = nomeRegione.replace("\\", "\\\\").replace("\"", "\\\"");

                sb.append(String.format(java.util.Locale.US,
                        "{" +
                                "\"id_multa\":%d," +
                                "\"targa\":\"%s\"," +
                                "\"importo\":%.2f," +
                                "\"data\":\"%s\"," +
                                "\"pagato\":%s," +
                                "\"nome_regione\":\"%s\"" +
                                "}",
                        id,
                        targa,
                        imp,
                        data != null ? data.toString() : "",
                        pagato ? "true" : "false",
                        nomeRegione
                ));
            }
            sb.append("]");
            return sb.toString();
        }
    }

}
