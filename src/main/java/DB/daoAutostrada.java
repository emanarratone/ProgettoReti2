package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class daoAutostrada {
    public String getRegioniAutostradeCaselli() throws SQLException {
        String SQL =
                "SELECT nome, nome_autostrada, nome_casello\n" +
                        "FROM (\n" +
                        "  SELECT\n" +
                        "    r.id_regione,\n" +
                        "    r.nome  AS nome,\n" +
                        "    a.citta AS nome_autostrada,\n" +
                        "    c.nome  AS nome_casello,\n" +
                        "    row_number() OVER (\n" +
                        "      PARTITION BY r.id_regione\n" +
                        "      ORDER BY random()\n" +
                        "    ) AS rn\n" +
                        "  FROM REGIONE r\n" +
                        "  JOIN AUTOSTRADA a ON a.regione = r.id_regione\n" +
                        "  JOIN CASELLO   c ON c.id_autostrada = a.id_autostrada\n" +
                        ") sub\n" +
                        "WHERE rn = 1\n" +                     // una riga per regione
                        "ORDER BY id_regione\n" +
                        "LIMIT 5;";                            // al massimo 5 regioni


        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder sb = new StringBuilder();
            sb.append("[");

            boolean first = true;
            while (rs.next()) {
                if (!first) sb.append(",");
                first = false;

                String nomeReg = rs.getString("nome");
                String nomeAut = rs.getString("nome_autostrada");
                String nomeCas = rs.getString("nome_casello");

                // escape minimale per JSON
                nomeReg = nomeReg.replace("\\", "\\\\").replace("\"", "\\\"");
                nomeAut = nomeAut.replace("\\", "\\\\").replace("\"", "\\\"");
                nomeCas = nomeCas.replace("\\", "\\\\").replace("\"", "\\\"");

                sb.append(String.format(java.util.Locale.US,
                        "{\"nome\":\"%s\",\"nome_autostrada\":\"%s\",\"nome_casello\":\"%s\"}",
                        nomeReg, nomeAut, nomeCas));
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public String getAutostradeJson() throws SQLException {
        String sql =
                "SELECT a.id_autostrada, a.citta AS nome_autostrada, r.nome AS nome " +
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
                String regione = rs.getString("nome");

                sb.append("{")
                        .append("\"id_autostrada\":").append(id).append(",")
                        .append("\"nome_autostrada\":\"").append(nome).append("\",")
                        .append("\"nome\":\"").append(regione != null ? regione : "").append("\"")
                        .append("}");
            }

            sb.append("]");
            return sb.toString();
        }
    }

    public String getRegioniJson() throws SQLException {
        String sql = "SELECT id_regione, nome FROM regione ORDER BY nome";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder sb = new StringBuilder();
            sb.append("[");

            boolean first = true;
            while (rs.next()) {
                if (!first) {
                    sb.append(",");
                }
                first = false;

                int id = rs.getInt("id_regione");
                String nome = rs.getString("nome");

                sb.append("{")
                        .append("\"id_regione\":").append(id).append(",")
                        .append("\"nome\":\"").append(escapeJson(nome)).append("\"")
                        .append("}");
            }

            sb.append("]");
            return sb.toString();
        }
    }
    public String getAutostradePerRegioneJson(int idRegione) throws SQLException {
        String sql =
                "SELECT a.id_autostrada, a.citta AS nome_autostrada, r.nome AS nome_regione " +
                        "FROM AUTOSTRADA a " +
                        "JOIN REGIONE r ON a.regione = r.id_regione " +
                        "WHERE a.regione = ? " +
                        "ORDER BY a.citta";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRegione);

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");

                boolean first = true;
                while (rs.next()) {
                    if (!first) {
                        sb.append(",");
                    }
                    first = false;

                    int idAutostrada      = rs.getInt("id_autostrada");
                    String nomeAutostrada = rs.getString("nome_autostrada");   // alias di citta
                    String nomeRegione    = rs.getString("nome_regione");      // alias di r.nome

                    sb.append("{")
                            .append("\"id_autostrada\":").append(idAutostrada).append(",")
                            .append("\"nome_autostrada\":\"").append(escapeJson(nomeAutostrada)).append("\",")
                            .append("\"nome\":\"").append(escapeJson(nomeRegione)).append("\"")
                            .append("}");
                }

                sb.append("]");
                return sb.toString();
            }
        }
    }

    // utility semplice per escape delle stringhe JSON
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

}
