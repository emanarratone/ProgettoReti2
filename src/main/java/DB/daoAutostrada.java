package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class daoAutostrada {
    public String getRegioniAutostradeCaselli() throws SQLException {
        String SQL =
                "SELECT " +
                        "    r.nome  AS nome_regione, " +
                        "    a.citta AS nome_autostrada, " +
                        "    c.nome  AS nome_casello " +
                        "FROM REGIONE r " +
                        "JOIN AUTOSTRADA a " +
                        "  ON a.regione = r.id_regione " +
                        "JOIN CASELLO c " +
                        "  ON c.id_autostrada = a.id_autostrada " +
                        "WHERE c.id_casello = ( " +
                        "    SELECT MIN(c2.id_casello) " +
                        "    FROM CASELLO c2 " +
                        "    WHERE c2.id_autostrada = a.id_autostrada " +
                        ") " +
                        "ORDER BY r.id_regione, a.id_autostrada";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder sb = new StringBuilder();
            sb.append("[");

            boolean first = true;
            while (rs.next()) {
                if (!first) sb.append(",");
                first = false;

                String nomeReg = rs.getString("nome_regione");
                String nomeAut = rs.getString("nome_autostrada");
                String nomeCas = rs.getString("nome_casello");

                // escape minimale per JSON
                nomeReg = nomeReg.replace("\\", "\\\\").replace("\"", "\\\"");
                nomeAut = nomeAut.replace("\\", "\\\\").replace("\"", "\\\"");
                nomeCas = nomeCas.replace("\\", "\\\\").replace("\"", "\\\"");

                sb.append(String.format(java.util.Locale.US,
                        "{\"nome_regione\":\"%s\",\"nome_autostrada\":\"%s\",\"nome_casello\":\"%s\"}",
                        nomeReg, nomeAut, nomeCas));
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public String getAutostradeJson() throws SQLException {
        String sql =
                "SELECT a.id_autostrada, a.citta AS nome_autostrada, r.nome AS nome_regione " +
                        "FROM AUTOSTRADA a " +
                        "LEFT JOIN REGIONE r ON a.regione = r.id_regione " +
                        "ORDER BY r.nome, a.citta";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder sb = new StringBuilder();
            sb.append("[");
            boolean first = true;

            while (rs.next()) {
                if (!first) sb.append(",");
                first = false;

                int id         = rs.getInt("id_autostrada");
                String nome    = rs.getString("nome_autostrada"); // alias di citta
                String regione = rs.getString("nome_regione");

                sb.append("{")
                        .append("\"id_autostrada\":").append(id).append(",")
                        .append("\"nome_autostrada\":\"").append(nome).append("\",")
                        .append("\"nome_regione\":\"").append(regione != null ? regione : "").append("\"")
                        .append("}");
            }

            sb.append("]");
            return sb.toString();
        }
    }


}
