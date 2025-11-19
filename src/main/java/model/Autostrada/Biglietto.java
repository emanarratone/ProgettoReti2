package model.Autostrada;

import java.util.ArrayList;
import java.time.LocalDateTime;

public class Biglietto {
    private String Matricola;
    private String ID_Totem;
    private Auto auto;
    private LocalDateTime timestamp_in;
    private Casello casello_in;


    public Biglietto(String Matricola, String ID_Totem, Auto auto, LocalDateTime timestamp_in, Casello casello_in) {
        this.Matricola = Matricola;
        this.ID_Totem = ID_Totem;
        this.auto = auto;
        this.timestamp_in = timestamp_in;
        this.casello_in = casello_in;
    }

    public String getMatricola() {
        return Matricola;
    }

    public void setMatricola(String matricola) {
        Matricola = matricola;
    }

    public String getID_Totem() {
        return ID_Totem;
    }

    public void setID_Totem(String ID_Totem) {
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
