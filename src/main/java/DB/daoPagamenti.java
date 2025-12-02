package DB;

import model.Autostrada.Pagamento;

import java.sql.*;

public class daoPagamenti {

    // Pagamenti da incassare: adatta il valore di stato a ciò che usi nel DB
    private static final String STATO_DA_INCASSARE = "PENDING";

    public int contaPagamentiDaIncassare() throws SQLException {
        String sql = "SELECT COUNT(*) " +
                "FROM Pagamento " +
                "WHERE stato = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, STATO_DA_INCASSARE);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public ResponseEntity<String> insertPagamenti(Pagamento p)  throws SQLException {
        String sql = "INSERT INTO Pagamento (id_pagamento, id_biglietto, importo, stato, timestamp_out, casello_out) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getID_transazione());
            ps.setInt(2, p.getBiglietto().getID_biglietto());
            ps.setDouble(3, p.getPrezzo());
            ps.setString(4, (p.getStatus())? "PAGATO" : "NON PAGATO");
            ps.setDate(5, new Date(p.getTimestamp_out().getYear(),
                    p.getTimestamp_out().getMonth().getValue(),
                    p.getTimestamp_out().getDayOfMonth()));
            ps.setInt(6, p.getCasello_out().getId());
        }
    }

    public ResponseEntity<String> updatePagamento(int id, Boolean nuovoStato) {
        String sql = "UPDATE Pagamento SET Stato = ? WHERE id_pagamento = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, nuovoStato);
            ps.setInt(2, id);
            int righeAggiornate = ps.executeUpdate();
            if (righeAggiornate > 0) {
                return ResponseEntity.ok("{\"message\":\"Pagamento aggiornato con successo\"}");
            } else {
                return ResponseEntity.status(404).body("{\"error\":\"Pagamento non trovato\"}");
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno durante l'aggiornamento\"}");
        }
    }


    public ResponseEntity<String> deletePagamento(int id) {
        String sql = "DELETE FROM Pagamento WHERE id_pagamento = ?";

        try (Connection conn = DbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, id);
                int righeEliminate = ps.executeUpdate();

                if (righeEliminate == 0) {
                    conn.rollback();
                    return ResponseEntity.status(404).body("{\"error\":\"Pagamento non trovato\"}");
                }

                conn.commit();
                return ResponseEntity.ok("{\"message\":\"Pagamento eliminato con successo\"}");
            } catch (SQLException ex) {
                conn.rollback();
                return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno durante l'eliminazione\"}");
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore di connessione al database\"}");
        }
    }

}
