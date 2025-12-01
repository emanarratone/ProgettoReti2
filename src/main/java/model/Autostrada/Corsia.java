package model.Autostrada;


import model.Dispositivi.Dispositivi;

import java.util.ArrayList;
import java.util.List;

public class Corsia {

    private final String casello;
    private final Integer ID;
    private Tipo tipo;
    public enum Tipo { ENTRATA, USCITA }
    private Boolean isClosed;

    public Corsia(String casello,Integer ID,  boolean verso) {
        this.casello = casello;
        this.ID = ID;

        this.isClosed = false;
    }

    public Boolean getClosed() {
        return isClosed;
    }

    public void setClosed(Boolean closed) {
        isClosed = closed;
    }

    public String getCasello() {
        return casello;
    }

    public Tipo getTipo() {
        return
    }

    public Integer getID() {
        return ID;
    }

    @Override
    public String toString() {
        return "Casello "+ this.casello + "\n" +
                "ID "+ this.ID + "\n" +
                "Stato: " + (this.isClosed ? "aperto" : "chiuso");
    }

}
