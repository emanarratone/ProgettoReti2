package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.postgresql.core.Oid.INTERVAL;

public class daoMulte {

    public int contaMulteUltime24h() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Multe WHERE timestamp_multa >= NOW() - INTERVAL '24 hours' ";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }
}
