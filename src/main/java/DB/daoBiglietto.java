package DB;

import model.Autostrada.Biglietto;

import java.sql.*;

public class daoBiglietto {

    public void insertBiglietto(Biglietto biglietto) throws SQLException {

        String sql = "INSERT INTO BIGLIETTO (id_biglietto, matricola, targa_auto, classe_veicolo, timestamp_in, id_casello_in) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DbConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, biglietto.getID_biglietto());
            ps.setInt(2, biglietto.getID_Totem());
            ps.setString(3, biglietto.getAuto().getTarga());
            ps.setString(4, biglietto.getAuto().getTipoVeicolo().toString());
            ps.setTimestamp(5, Timestamp.valueOf(biglietto.getTimestamp_in()));
            ps.setInt(6, biglietto.getCasello_in().getId());

            ps.executeUpdate();
        }
    }
}
