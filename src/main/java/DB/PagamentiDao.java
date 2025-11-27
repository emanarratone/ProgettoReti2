package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PagamentiDao {

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
}
