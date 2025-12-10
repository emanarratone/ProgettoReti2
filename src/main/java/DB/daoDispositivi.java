package DB;

import model.Dispositivi.*;
import org.springframework.http.ResponseEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import static DB.DbConnection.getConnection;

public class daoDispositivi {

    // INSERT dispositivo (POST /lanes/{idCorsia}/devices)
    public void insertDispositivo(String stato,Integer numCorsia, String tipo, Integer idCasello) throws SQLException {

        String sql = """
            INSERT INTO DISPOSITIVO (stato, num_corsia, id_casello, tipo_dispositivo)
            VALUES (?, ?, ?, ?)
            """;
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, stato);
            ps.setInt(2, numCorsia);
            ps.setInt(3, idCasello);
            ps.setString(4, tipo);
            ps.executeUpdate();
        }
    }

    public String  getTipoDispositivo(Dispositivi d) {
        if(d instanceof Sbarra) return "SBARRA";
        else if(d instanceof Telecamera) return "TELECAMERA";
        else return "TOTEM";
    }

    // UPDATE dispositivo (PUT /devices/{idDispositivo})
    public void updateDispositivo(Integer id, String status) throws SQLException {
        String sql = "UPDATE DISPOSITIVO SET stato = ? WHERE id_dispositivo = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    // DELETE dispositivo (DELETE /devices/{idDispositivo})
    public void deleteDispositivo(int idDispositivo) throws SQLException {
        String sql = "DELETE FROM DISPOSITIVO WHERE id_dispositivo = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idDispositivo);
            ps.executeUpdate();
        }
    }

    public  int contaDispositivi() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Dispositivo";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1);
        }
    }

    // GET /lanes/{idCorsia}/devices
    // qui semplifichiamo: assumiamo che idCorsia == num_corsia e recuperiamo tutte le righe con quel num_corsia
    public String getDispositiviPerCorsiaJson(int numCorsia, int id_casello) throws SQLException {
        String sql = """
        SELECT d.id_dispositivo,
               d.stato,
               d.tipo_dispositivo,
               d.num_corsia,
               d.id_casello
        FROM DISPOSITIVO d
        WHERE d.num_corsia = ? AND d.id_casello = ?
        """;

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, numCorsia);
            ps.setInt(2, id_casello);

            try (ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;

                    int id = rs.getInt("id_dispositivo");
                    String stato = rs.getString("stato");
                    String tipo = rs.getString("tipo_dispositivo");

                    sb.append(String.format(Locale.US,
                            "{\"id_dispositivo\":%d,\"tipo\":\"%s\",\"stato\":\"%s\"}",
                            id,
                            tipo.replace("\"", "\\\""),
                            stato.replace("\"", "\\\"")));
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }


}
