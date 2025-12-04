package DB;

import model.Autostrada.Autostrada;
import model.Autostrada.Casello;
import model.Autostrada.Regione;
import org.springframework.http.ResponseEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public void insertAutostrada(Autostrada a) throws SQLException {
        String sql = "INSERT INTO AUTOSTRADA (citta, id_regione) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getCittà());
            ps.setInt(2, a.getRegione());
            ps.executeUpdate();
        }
    }



    public ResponseEntity<String> aggiornaAutostrada(Autostrada a1, Autostrada a2) throws SQLException {
        String sql = "UPDATE AUTOSTRADA SET id_autostrada=?, citta=?, regione=? WHERE id_autostrada=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a2.getID());
            ps.setString(2, a2.getCittà());
            ps.setInt(3, a2.getRegione());
            ps.setInt(4, a1.getID());
            ps.executeUpdate();
            if (ps.executeUpdate() > 0) {
                return ResponseEntity.ok("{\"message\":\"Autostrada aggiornata con successo\"}");
            } else {
                return ResponseEntity.status(404).body("{\"error\":\"Autostrada non trovata\"}");
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno durante l'aggiornamento\"}");
        }
    }

    public ResponseEntity<String> eliminaAutostrada(Autostrada a) throws SQLException {
        String sql = "DELETE FROM Autostrada WHERE id_autostrada = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getID());
            ps.executeUpdate();
            if (ps.executeUpdate() > 0) {
                return ResponseEntity.ok("{\"message\":\"Autostrada eliminata con successo\"}");
            } else {
                return ResponseEntity.status(404).body("{\"error\":\"Autostrada non trovata\"}");
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno durante l'eliminazione\"}");
        }
    }

    public String getregioneJson() throws SQLException {
        String sql = "SELECT id_regione, nome FROM regione ORDER BY nome";
        try (Connection conn = getConnection();
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
        String sql = """
        SELECT id_autostrada, citta AS nome_autostrada
        FROM AUTOSTRADA
        WHERE id_regione = ?
        ORDER BY id_autostrada
    """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRegione);
            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;
                    int id = rs.getInt("id_autostrada");
                    String nome = rs.getString("nome_autostrada");
                    sb.append("{")
                            .append("\"id_autostrada\":").append(id).append(",")
                            .append("\"nome_autostrada\":\"").append(nome).append("\"")
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

    public void insertRegione(Regione r) throws SQLException {
        String sql = "INSERT INTO regione (nome) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getNomeRegione());
            ps.executeUpdate();
        }
    }

    public void updateRegione(int idRegione, Regione r) throws SQLException {
        String sql = "UPDATE regione SET nome = ? WHERE id_regione = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getNomeRegione());
            ps.setInt(2, idRegione);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Nessuna regione aggiornata, id=" + idRegione);
            }
        }
    }

    public void deleteRegione(int idRegione) throws SQLException {
        String sql = "DELETE FROM regione WHERE id_regione = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRegione);
            ps.executeUpdate();
        }
    }

    // UPDATE
    public void updateAutostrada(int idAutostrada, Autostrada a) throws SQLException {
        String sql = "UPDATE AUTOSTRADA SET citta = ?, id_regione = ? WHERE id_autostrada = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getCittà());
            ps.setInt(2, a.getRegione());
            ps.setInt(3, idAutostrada);
            ps.executeUpdate();
        }
    }

    // DELETE
    public void deleteAutostrada(int idAutostrada) throws SQLException {
        String sql = "DELETE FROM AUTOSTRADA WHERE id_autostrada = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAutostrada);
            ps.executeUpdate();
        }
    }


}
