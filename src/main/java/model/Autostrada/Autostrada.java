package model.Autostrada;

import java.util.ArrayList;

public class Autostrada {

    private final Integer ID;
    private final String città;
    private final String regione;

    public Autostrada(Integer ID, String città, String regione, String casello) {
        this.ID = ID;
        this.città = città;
        this.regione = regione;
    }

    public Integer getID() {
        return ID;
    }

    public String getCittà() {
        return città;
    }

    public String getRegione() {
        return regione;
    }

}

