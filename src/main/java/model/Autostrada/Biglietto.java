package model.Autostrada;

import java.time.LocalDateTime;

public class Biglietto {
    private Integer ID_biglietto;
    private Integer ID_Totem;
    private Auto auto;
    private LocalDateTime timestamp_in;
    private Casello casello_in;


    public Biglietto(Integer ID_biglietto, Integer ID_Totem, Auto auto, LocalDateTime timestamp_in, Casello casello_in) {
        this.ID_biglietto = ID_biglietto;
        this.ID_Totem = ID_Totem;
        this.auto = auto;
        this.timestamp_in = timestamp_in;
        this.casello_in = casello_in;
    }

    public Integer getID_biglietto() {
        return ID_biglietto;
    }

    public void setID_biglietto(Integer ID_biglietto) {
        this.ID_biglietto = ID_biglietto;
    }

    public Integer getID_Totem() {
        return ID_Totem;
    }

    public void setID_Totem(Integer ID_Totem) {
        this.ID_Totem = ID_Totem;
    }

    public Auto getAuto() {
        return auto;
    }

    public void setAuto(Auto auto) {
        this.auto = auto;
    }

    public LocalDateTime getTimestamp_in() {
        return timestamp_in;
    }

    public void setTimestamp_in(LocalDateTime timestamp_in) {
        this.timestamp_in = timestamp_in;
    }

    public Casello getCasello_in() {
        return casello_in;
    }

    public void setCasello_in(Casello casello_in) {
        this.casello_in = casello_in;
    }
}
