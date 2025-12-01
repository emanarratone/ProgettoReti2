package DB;

import model.Autostrada.Corsia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class daoCorsie {

    public void inserisciCorsia(Corsia c) throws SQLException {
        String sql = "INSERT INTO Corsia (id_corsia, id_casello, verso, tipo) VALUES (?,?,?,?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getID());
            ps.setString(2, c.getCasello());
            ps.setString(3, c.);
            ps.executeUpdate();
        }
    }

    public void aggiornaCorsia(int numCorsia, String nuovoVerso) throws SQLException {
        String sql = "UPDATE Corsia SET verso = ? WHERE Num_corsia = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuovoVerso);
            ps.setInt(2, numCorsia);
            ps.executeUpdate();
        }
    }

    public void eliminaCorsia(int numCorsia) throws SQLException {
        String sql = "DELETE FROM Corsia WHERE Num_corsia = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numCorsia);
            ps.executeUpdate();
        }
    }
    
    public  int contaCorsie() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Corsia";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }

    public String getCorsiePerCaselloJson(int idCasello) throws SQLException {
        String sql =
                "SELECT id_corsia, verso, numero_corsia, tipo_corsia " +
                        "FROM CORSIA " +
                        "WHERE id_casello = ? " +
                        "ORDER BY numero_corsia";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCasello);

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                boolean first = true;

                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;

                    int id       = rs.getInt("id_corsia");
                    String verso = rs.getString("verso");
                    int num      = rs.getInt("numero_corsia");
                    String tipo  = rs.getString("tipo_corsia");

                    sb.append("{")
                            .append("\"id_corsia\":").append(id).append(",")
                            .append("\"nome_corsia\":\"Corsia ").append(num).append("\",")
                            .append("\"direzione\":\"").append(verso).append("\",")
                            .append("\"tipo_corsia\":\"").append(tipo).append("\"")
                            .append("}");
                }

                sb.append("]");
                return sb.toString();
            }
        }
    }

}
