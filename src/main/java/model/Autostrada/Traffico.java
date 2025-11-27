package model.Autostrada;

public class Traffico {
    private int mediaLast30Days;
    private int trafficToday;
    private double percentageChangeVsYesterday;

    // Costruttore
    public Traffico(int mediaLast30Days, int trafficToday, double percentageChangeVsYesterday) {
        this.mediaLast30Days = mediaLast30Days;
        this.trafficToday = trafficToday;
        this.percentageChangeVsYesterday = percentageChangeVsYesterday;
    }

    // Getter
    public int getMediaLast30Days() { return mediaLast30Days; }
    public int getTrafficToday() { return trafficToday; }
    public double getPercentageChangeVsYesterday() { return percentageChangeVsYesterday; }

    // Setter (se serve)
}
