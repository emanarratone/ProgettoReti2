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

    public void insertPagamenti(Pagamento p)  throws SQLException {
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
}
