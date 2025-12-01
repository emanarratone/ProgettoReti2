package DB;

import model.Autostrada.Auto;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class daoVeicoli {

    public void insertVeicoli(Auto a)  throws SQLException {
        String s = "INSERT INTO Auto (targa, classe_veicolo) VALUES (?,?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(s)) {
            ps.setString(1, a.getTarga());
            ps.setString(2, a.getTipoVeicolo().toString());
        }
    }


    public String getUltimiPassaggiPerTargaJson(String targa) throws SQLException {
        String sql =
                "SELECT " +
                        "  b.timestamp_in, " +
                        "  c_in.nome   AS casello_ingresso, " +
                        "  p.timestamp_out, " +
                        "  c_out.nome  AS casello_uscita, " +
                        "  p.importo " +
                        "FROM biglietto b " +
                        "JOIN pagamento p   ON p.id_biglietto = b.id_biglietto " +
                        "JOIN casello   c_in  ON c_in.id_casello  = b.casello_in " +
                        "JOIN casello   c_out ON c_out.id_casello = p.casello_out " +
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
                            .append("\"timestampIn\":\"").append(tin.toLocalDateTime().format(fmt)).append("\",")
                            .append("\"caselloIn\":\"").append(cin).append("\",")
                            .append("\"timestampOut\":\"").append(tout.toLocalDateTime().format(fmt)).append("\",")
                            .append("\"caselloOut\":\"").append(cout).append("\",")
                            .append("\"importo\":").append(String.format(Locale.US, "%.2f", importo))
                            .append("}");
                }

                sb.append("]");
                return sb.toString();
            }
        }
    }
}
