package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class daoBiglietto {

    public static long countAutoOggi() throws SQLException {

        String SQL =
                "SELECT COUNT(*) AS auto_oggi " +
                        "FROM Biglietto " +
                        "WHERE timestamp_in::date = CURRENT_DATE";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong("auto_oggi");
            }
            return 0L;
        }
    }
}
