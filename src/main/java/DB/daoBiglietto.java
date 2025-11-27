package DB;

import model.Autostrada.Biglietto;

import java.sql.*;

public class daoBiglietto {

    public void insertBiglietto(Biglietto biglietto) throws SQLException {

        String sql = "INSERT INTO BIGLIETTO (matricola, id_totem, targa_auto, timestamp_in, id_casello_in) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DbConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, biglietto.getMatricola());
            ps.setString(2, biglietto.getID_Totem());
            ps.setString(3, biglietto.getAuto().getTarga());
            ps.setTimestamp(4, Timestamp.valueOf(biglietto.getTimestamp_in()));
            ps.setString(5, biglietto.getCasello_in().getSigla());

            ps.executeUpdate();
        }
    }
}
