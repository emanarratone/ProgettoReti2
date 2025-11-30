package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class daoDispositivi {

    public void inserisciDispositivo(int id, String stato, int numCorsia, String sigla) throws SQLException {
        String sqlElenco = "INSERT INTO Elenco (Num_corsia, sigla, ID) VALUES (?, ?, ?)";
        String sqlDisp   = "INSERT INTO Dispositivo (ID, Stato, Num_corsia, sigla) VALUES (?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psE = conn.prepareStatement(sqlElenco);
                 PreparedStatement psD = conn.prepareStatement(sqlDisp)) {

                psE.setInt(1, numCorsia);
                psE.setString(2, sigla);
                psE.setInt(3, id);
                psE.executeUpdate();

                psD.setInt(1, id);
                psD.setString(2, stato);
                psD.setInt(3, numCorsia);
                psD.setString(4, sigla);
                psD.executeUpdate();

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void aggiornaDispositivo(int id, String nuovoStato) throws SQLException {
        String sql = "UPDATE Dispositivo SET Stato = ? WHERE ID = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuovoStato);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void eliminaDispositivo(int id) throws SQLException {
        String sqlDisp   = "DELETE FROM Dispositivo WHERE ID = ?";
        String sqlElenco = "DELETE FROM Elenco      WHERE ID = ?";

        try (Connection conn = DbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psD = conn.prepareStatement(sqlDisp);
                 PreparedStatement psE = conn.prepareStatement(sqlElenco)) {

                psD.setInt(1, id);
                psD.executeUpdate();

                psE.setInt(1, id);
                psE.executeUpdate();

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    public  int contaDispositivi() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Dispositivo";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }

    public String getDispositiviPerCorsiaJson(int idCorsia) throws SQLException {
        String sql =
                "SELECT id_dispositivo, stato, tipo_dispositivo " +
                        "FROM DISPOSITIVO " +
                        "WHERE id_corsia = ? " +
                        "ORDER BY id_dispositivo";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCorsia);

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                boolean first = true;

                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;

                    int id        = rs.getInt("id_dispositivo");
                    String stato  = rs.getString("stato");
                    String tipo   = rs.getString("tipo_dispositivo");

                    sb.append("{")
                            .append("\"id_dispositivo\":").append(id).append(",")
                            .append("\"tipo\":\"").append(tipo).append("\",")
                            .append("\"posizione\":\"").append(stato).append("\"")
                            .append("}");
                }

                sb.append("]");
                return sb.toString();
            }
        }
    }


}
