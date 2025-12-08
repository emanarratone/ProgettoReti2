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
        String sql = "INSERT INTO Pagamento (id_biglietto, importo, stato, timestamp_out, casello_out) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getBiglietto());
            ps.setDouble(2, p.getPrezzo());
            ps.setString(3, (p.getStatus())? "PAGATO" : "NON PAGATO");
            ps.setTimestamp(4, valueOf(p.getTimestamp_out()));
            ps.setInt(5, p.getCasello_out());
            int n = ps.executeUpdate();
            if (n > 0) {
                return ResponseEntity.ok("{\"message\":\"Pagamento aggiornato con successo\"}");
            } else {
                return ResponseEntity.status(404).body("{\"error\":\"Pagamento non trovato\"}");
            }
        }
    }

   public ResponseEntity<String> updatePagamento(Integer idPagamento, Pagamento p) throws SQLException {
       String sql = "UPDATE Pagamento SET Stato = ? WHERE id_pagamento = ?";
       try (Connection conn = DbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
           ps.setBoolean(1, p.getStatus());
           ps.setInt(2, idPagamento);
           int righeAggiornate = ps.executeUpdate();
           if (righeAggiornate > 0) {
               return ResponseEntity.ok("{\"message\":\"Pagamento aggiornato con successo\"}");
           } else {
               return ResponseEntity.status(404).body("{\"error\":\"Pagamento non trovato\"}");
           }
       }
   }


    public ResponseEntity<String> deletePagamento(Integer idPagamento) throws SQLException {
        String sql = "DELETE FROM Pagamento WHERE id_pagamento = ?";

        try (Connection conn = DbConnection.getConnection();
              PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idPagamento);
                int righeEliminate = ps.executeUpdate();

                if (righeEliminate == 0) {
                    return ResponseEntity.status(404).body("{\"error\":\"Pagamento non trovato\"}");
                }

                return ResponseEntity.ok("{\"message\":\"Pagamento eliminato con successo\"}");
        }
    }
}
