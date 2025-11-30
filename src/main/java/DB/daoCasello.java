package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class daoCasello {
    public void inserisciCasello(String sigla) throws SQLException {
        String sql = "INSERT INTO Casello (Sigla) VALUES (?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sigla);
            ps.executeUpdate();
        }
    }

    public void aggiornaCasello(String vecchiaSigla, String nuovaSigla) throws SQLException {
        String sql = "UPDATE Casello SET Sigla = ? WHERE Sigla = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuovaSigla);
            ps.setString(2, vecchiaSigla);
            ps.executeUpdate();
        }
    }

    public void eliminaCasello(String sigla) throws SQLException {
        String sql = "DELETE FROM Casello WHERE Sigla = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sigla);
            ps.executeUpdate();
        }
    }

    public int contaCaselli() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Casello";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }

    public String getCaselliPerAutostrada(int idAutostrada) throws SQLException {
        String sql =
                "SELECT c.id_casello, c.nome, c.sigla, acc.progressiva_km " +
                        "FROM CASELLO c " +
                        "JOIN AUTOSTRADA_CONTIENE_CASELLO acc ON acc.id_casello = c.id_casello " +
                        "WHERE acc.id_autostrada = ? " +
                        "ORDER BY acc.progressiva_km, c.nome";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAutostrada);

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                boolean first = true;

                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;

                    int id        = rs.getInt("id_casello");
                    String nome   = rs.getString("nome");
                    String sigla  = rs.getString("sigla");
                    double km     = rs.getDouble("progressiva_km");

                    sb.append("{")
                            .append("\"id_casello\":").append(id).append(",")
                            .append("\"nome_casello\":\"").append(nome).append("\",")
                            .append("\"sigla\":\"").append(sigla).append("\",")
                            .append("\"km\":").append(km)
                            .append("}");
                }

                sb.append("]");
                return sb.toString();
            }
        }
    }

}
