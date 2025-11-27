package DB;

import model.Autostrada.Traffico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class TrafficoDao {
    public static long countAutoOggi() throws SQLException {
        String SQL = "SELECT COUNT(*) AS auto_oggi FROM Biglietto WHERE timestamp_in::date = CURRENT_DATE";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong("auto_oggi");
            }
            return 0L;
        }
    }

    public static double mediaUltimi30Giorni() throws SQLException {
        String SQL = "SELECT AVG(auto_giornaliero) AS media_30_giorni FROM ( " +
                "SELECT timestamp_in::date AS giorno, COUNT(*) AS auto_giornaliero FROM Biglietto " +
                "WHERE timestamp_in::date >= CURRENT_DATE - INTERVAL '30 days' " +
                "GROUP BY giorno) AS daily_counts";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("media_30_giorni");
            }
            return 0.0;
        }
    }

    public static long countAutoIeri() throws SQLException {
        String SQL = "SELECT COUNT(*) AS auto_ieri FROM Biglietto WHERE timestamp_in::date = CURRENT_DATE - INTERVAL '1 day'";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong("auto_ieri");
            }
            return 0L;
        }
    }

    public Traffico calcolaKpiTraffico() throws SQLException {
        long trafficToday = countAutoOggi();
        long trafficYesterday = countAutoIeri();
        double averageLast30Days = mediaUltimi30Giorni();

        double variationPercent = 0.0;
        if (trafficYesterday != 0) {
            variationPercent = ((trafficToday - trafficYesterday) / (double) trafficYesterday) * 100.0;
        }

        return new Traffico(
                (int) Math.round(averageLast30Days),
                (int) trafficToday,
                variationPercent);
    }
}