package DB;

import model.Autostrada.Multa;
import org.springframework.http.ResponseEntity;

import java.sql.*;

import static java.sql.Timestamp.valueOf;


public class daoMulte {

    public int contaMulteUltime24h() throws SQLException {
        String sql = "SELECT COUNT(*) " +
                "FROM Multa " +
                "WHERE data >= NOW() - INTERVAL '24 hours' ";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {        //non abbiamo il campo data in multe, bisogna fare una get del biglietto della multa e prendere il dato da li

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
                        "LEFT JOIN BIGLIETTO b  ON b.id_biglietto  = m.id_biglietto " +
                        "LEFT JOIN CASELLO c    ON c.id_casello    = b.casello_in " +
                        "LEFT JOIN AUTOSTRADA a ON a.id_autostrada = c.id_autostrada " +
                        "LEFT JOIN REGIONE r    ON r.id_regione    = a.id_regione";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {        //non abbiamo il campo data in multe, bisogna fare una get del biglietto della multa e prendere il dato da li

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

    public ResponseEntity<String> insertMulta(Multa m) throws SQLException {
        String s = "INSERT INTO MULTA (id_multa, targa, importo, data, pagato, id_biglietto) VALUES (?,?,?,?,?,?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(s)) {
            ps.setInt(1, m.getId());
            ps.setString(2, m.getTarga());
            ps.setDouble(3, m.getImporto());
            ps.setTimestamp(4, valueOf(m.getData()));
            ps.setBoolean(5, m.getPagato());
            ps.setInt(6, m.getBiglietto());

            int righeInserite = ps.executeUpdate();
            if (righeInserite > 0) {
                return ResponseEntity.ok("{\"message\":\"Multa inserita con successo\"}");
            } else {
                return ResponseEntity.internalServerError().body("{\"error\":\"Inserimento multa fallito\"}");
            }
        }
    }



    public ResponseEntity<String> updateMulta(Multa m1, Multa m2) throws SQLException {
        String sql = "UPDATE Multa SET Pagato = ? WHERE id_multa = ?";  // Corretta a tabella Multa
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, m2.getPagato());
            ps.setInt(2, m1.getId());
            int righeAggiornate = ps.executeUpdate();
            if (righeAggiornate > 0) {
                return ResponseEntity.ok("{\"message\":\"Multa aggiornata con successo\"}");
            } else {
                return ResponseEntity.status(404).body("{\"error\":\"Multa non trovata\"}");
            }
        }
    }

    public ResponseEntity<String> deleteMulta(Multa m) throws SQLException {
        String sql = "DELETE FROM Multa WHERE id_multa = ?";

        try (Connection conn = DbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, m.getId());
                int righeEliminate = ps.executeUpdate();

                if (righeEliminate == 0) {
                    conn.rollback();
                    return ResponseEntity.status(404).body("{\"error\":\"Multa non trovata\"}");
                }
                return ResponseEntity.ok("{\"message\":\"Multa eliminata con successo\"}");
            } catch (SQLException ex) {
                return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno durante l'eliminazione\"}");
            }
        }
    }

}
