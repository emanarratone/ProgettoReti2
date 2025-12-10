package DB;

import model.Autostrada.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class daoTraffico {
    public static long countAutoOggi() throws SQLException {
        String SQL = "SELECT COUNT(*) AS auto_oggi " +
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
        String SQL = "SELECT COUNT(*) AS auto_ieri " +
                "FROM Biglietto " +
                "WHERE timestamp_in::date = CURRENT_DATE - INTERVAL '1 day'";
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

    // Restituisce JSON: [ {"day":"2025-11-01","count":1234}, ... ]
    public String getTrendUltimi30GiorniJson() throws SQLException {
        String sql =
                "SELECT date_trunc('day', timestamp_in) AS day, " +
                        "       COUNT(*) AS count " +
                        "FROM biglietto " +                          // <- minuscolo
                        "WHERE timestamp_in >= CURRENT_DATE - INTERVAL '29 days' " +
                        "GROUP BY day " +
                        "ORDER BY day";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder sb = new StringBuilder();
            sb.append("[");

            boolean first = true;
            while (rs.next()) {
                if (!first) sb.append(",");
                first = false;

                String dayStr = rs.getTimestamp("day")
                        .toLocalDateTime()
                        .toLocalDate()
                        .toString();   // "YYYY-MM-DD"
                int count = rs.getInt("count");

                sb.append(String.format(java.util.Locale.US,
                        "{\"day\":\"%s\",\"count\":%d}", dayStr, count));
            }
            sb.append("]");
            return sb.toString();
        }
    }


    // Restituisce JSON: [ {"hour":0,"count":100}, {"hour":8,"count":350}, ... ]
    public String getPicchiOrariOggiJson() throws SQLException {
        String sql = "SELECT EXTRACT(HOUR FROM timestamp_in)::int AS hour, COUNT(*) AS count " +
                "FROM Biglietto " +
                "WHERE timestamp_in >= date_trunc('day', NOW())  AND timestamp_in <  date_trunc('day', NOW()) + INTERVAL '1 day'  " +
                "GROUP BY hour  " +
                "ORDER BY hour";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder sb = new StringBuilder();
            sb.append("[");

            boolean first = true;
            while (rs.next()) {
                if (!first) sb.append(",");
                first = false;

                int hour  = rs.getInt("hour");
                int count = rs.getInt("count");

                sb.append(String.format(java.util.Locale.US,
                        "{\"hour\":%d,\"count\":%d}", hour, count));
            }
            sb.append("]");
            return sb.toString();
        }
    }


}