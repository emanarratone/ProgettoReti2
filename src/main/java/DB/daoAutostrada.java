package DB;

import model.Autostrada.Autostrada;
import model.Autostrada.Casello;
import model.Autostrada.Regione;
import org.springframework.http.ResponseEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import static DB.DbConnection.getConnection;

public class daoAutostrada {
    public String getregioneAutostradeCaselli() throws SQLException {
                String SQL =
                "SELECT nome, nome_autostrada, nome_casello, id_regione\n" +
                        "FROM (\n" +
                        "  SELECT\n" +
                        "    r.id_regione,\n" +
                        "    r.nome  AS nome,\n" +
                        "    a.citta AS nome_autostrada,\n" +
                        "    c.sigla AS nome_casello,\n" +
                        "    row_number() OVER (\n" +
                                "      PARTITION BY r.id_regione\n" +
                                "      ORDER BY random()\n" +
                                "    ) AS rn\n" +
                                "  FROM REGIONE r\n" +
                                "  JOIN AUTOSTRADA a ON a.regione = r.nome\n" +  // confronto stringa con stringa
                                "  JOIN CASELLO   c ON c.id_autostrada = a.id_autostrada\n" +
                                ") sub\n" +
                                "WHERE rn = 1\n" +
                                "ORDER BY id_regione\n" +
                                "LIMIT 5;";

        try (Connection con = getConnection();
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
                        "LEFT JOIN REGIONE r ON a.id_regione = r.id_regione " +
                        "ORDER BY r.nome, a.citta";

        try (Connection con = getConnection();
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

    // INSERT autostrada (POST /api/highways)
    public void insertAutostrada(String citta, int idRegione) throws SQLException {
        String sql = "INSERT INTO AUTOSTRADA (citta, id_regione) VALUES (?, ?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, citta);
            ps.setInt(2, idRegione);
            ps.executeUpdate();
        }
    }


    // usato da GET /api/regions
    public String getregioneJson() throws SQLException {
        String sql = "SELECT id_regione, nome FROM REGIONE ORDER BY nome";
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean first = true;
            while (rs.next()) {
                if (!first) sb.append(",");
                first = false;
                int id = rs.getInt("id_regione");
                String nome = rs.getString("nome");
                sb.append(String.format(Locale.US,
                        "{\"id_regione\":%d,\"nome\":\"%s\"}",
                        id, nome.replace("\"", "\\\"")));
            }
        }
        sb.append("]");
        return sb.toString();
    }

    // GET /api/regions/{idRegione}/highways
    public String getAutostradePerRegioneJson(int idRegione) throws SQLException {
        String sql = """
            SELECT a.id_autostrada,
                   a.citta,
                   r.nome AS nome_regione
            FROM AUTOSTRADA a
            JOIN REGIONE r ON a.id_regione = r.id_regione
            WHERE a.id_regione = ?
            ORDER BY a.citta
            """;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idRegione);
            try (ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;
                    int id = rs.getInt("id_autostrada");
                    String citta = rs.getString("citta");
                    String nomeRegione = rs.getString("nome_regione");
                    sb.append(String.format(Locale.US,
                            "{\"id_autostrada\":%d,\"citta\":\"%s\",\"nome_regione\":\"%s\"}",
                            id,
                            citta.replace("\"", "\\\""),
                            nomeRegione.replace("\"", "\\\"")));
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }

    // utility semplice per escape delle stringhe JSON
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // INSERT regione (usato da POST /api/regions)
    public void insertRegione(String nome) throws SQLException {
        String sql = "INSERT INTO REGIONE (nome) VALUES (?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.executeUpdate();
        }
    }

    // UPDATE regione (PUT /api/regions/{idRegione})
    public void updateRegione(int idRegione, String nome) throws SQLException {
        String sql = "UPDATE REGIONE SET nome = ? WHERE id_regione = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setInt(2, idRegione);
            ps.executeUpdate();
        }
    }

    // DELETE regione
    public void deleteRegione(int idRegione) throws SQLException {
        String sql = "DELETE FROM REGIONE WHERE id_regione = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idRegione);
            ps.executeUpdate();
        }
    }

// UPDATE autostrada (PUT /api/highways/{idAutostrada})
    public void updateAutostrada(int idAutostrada, String citta, int idRegione) throws SQLException {
        String sql = "UPDATE AUTOSTRADA SET citta = ?, id_regione = ? WHERE id_autostrada = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, citta);
            ps.setInt(2, idRegione);
            ps.setInt(3, idAutostrada);
            ps.executeUpdate();
        }
    }


    // DELETE autostrada
    public void deleteAutostrada(int idAutostrada) throws SQLException {
        String sql = "DELETE FROM AUTOSTRADA WHERE id_autostrada = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idAutostrada);
            ps.executeUpdate();
        }
    }


}
