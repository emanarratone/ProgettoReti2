package DB;

import model.Autostrada.Pagamento;
import org.springframework.http.ResponseEntity;

import java.sql.*;

import static java.sql.Timestamp.valueOf;

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
            ps.setTimestamp(5, valueOf(p.getTimestamp_out()));
            ps.setInt(6, p.getCasello_out().getId());
            int n = ps.executeUpdate();
            if (n > 0) {
                return ResponseEntity.ok("{\"message\":\"Pagamento aggiornato con successo\"}");
            } else {
                return ResponseEntity.status(404).body("{\"error\":\"Pagamento non trovato\"}");
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno durante l'aggiornamento\"}");
        }
    }

   public ResponseEntity<String> updatePagamento(Pagamento p1, Pagamento p2) throws SQLException {
       String sql = "UPDATE Pagamento SET Stato = ? WHERE id_pagamento = ?";
       try (Connection conn = DbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
           ps.setBoolean(1, p2.getStatus());
           ps.setInt(2, p1.getID_transazione());
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


    public ResponseEntity<String> deletePagamento(Pagamento p) throws SQLException {
        String sql = "DELETE FROM Pagamento WHERE id_pagamento = ?";

        try (Connection conn = DbConnection.getConnection();
              PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, p.getID_transazione());
                int righeEliminate = ps.executeUpdate();

                if (righeEliminate == 0) {
                    return ResponseEntity.status(404).body("{\"error\":\"Pagamento non trovato\"}");
                }

                return ResponseEntity.ok("{\"message\":\"Pagamento eliminato con successo\"}");
        } catch (SQLException ex) {
            return ResponseEntity.internalServerError().body("{\"error\":\"Errore interno durante l'eliminazione\"}");
        }
    }
}
