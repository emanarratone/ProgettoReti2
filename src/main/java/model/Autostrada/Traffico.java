package model.Autostrada;

public class Traffico {
    private int mediaLast30Days;
    private int trafficToday;
    private double percentageChangeVsYesterday;

    public Traffico(int mediaLast30Days, int trafficToday, double percentageChangeVsYesterday) {
        this.mediaLast30Days = mediaLast30Days;
        this.trafficToday = trafficToday;
        this.percentageChangeVsYesterday = percentageChangeVsYesterday;
    }

    public int getMediaLast30Days() { return mediaLast30Days; }

    public void setMediaLast30Days(int mediaLast30Days) {
        this.mediaLast30Days = mediaLast30Days;
    }

    public int getTrafficToday() { return trafficToday; }


    public void setTrafficToday(int trafficToday) {
        this.trafficToday = trafficToday;
    }

    public double getPercentageChangeVsYesterday() { return percentageChangeVsYesterday; }

    public void setPercentageChangeVsYesterday(double percentageChangeVsYesterday) {
        this.percentageChangeVsYesterday = percentageChangeVsYesterday;
    }


    // Setter (se serve)
}
