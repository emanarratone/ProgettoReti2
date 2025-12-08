package DB;

import model.Autostrada.Regione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import static DB.DbConnection.getConnection;

public class daoRegione {
    // INSERT regione (usato da POST /api/regions)
    public void insertRegione(Regione r) throws SQLException {
        String sql = "INSERT INTO REGIONE (nome) VALUES (?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.getNomeRegione());
            ps.executeUpdate();
        }
    }

    // UPDATE regione (PUT /api/regions/{idRegione})
    public void updateRegione(Integer id, Regione r) throws SQLException {
        String sql = "UPDATE REGIONE SET nome = ? WHERE id_regione = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.getNomeRegione());
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    // DELETE regione
    public void deleteRegione(int idRegione) throws SQLException {
        String sql = "DELETE FROM REGIONE WHERE id_regione = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idRegione);
            ps.executeUpdate();
        }
    }

    // usato da GET /api/regions
    public String getregioneJson() throws SQLException {
        String sql = "SELECT id_regione, nome FROM REGIONE ORDER BY nome";
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean first = true;
            while (rs.next()) {
                if (!first) sb.append(",");
                first = false;
                int id = rs.getInt("id_regione");
                String nome = rs.getString("nome");
                sb.append(String.format(Locale.US,
                        "{\"id_regione\":%d,\"nome\":\"%s\"}",
                        id, nome.replace("\"", "\\\"")));
            }
        }
        sb.append("]");
        return sb.toString();
    }

}
