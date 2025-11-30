package model.Autostrada;

import java.util.ArrayList;

public class Autostrada {

    private final String ID;
    private final String città;
    private final Regione regione;
    private ArrayList<Casello> caselli;

    public Autostrada(String ID, String città, Regione regione, ArrayList<Casello> caselli) {
        this.ID = ID;
        this.città = città;
        this.regione = regione;
        this.caselli = caselli;
    }

    public Autostrada(String ID, String città, Regione regione) {
        this.ID = ID;
        this.città = città;
        this.regione = regione;
        this.caselli = new ArrayList<>();
    }

    public String getID() {
        return ID;
    }

    public String getCittà() {
        return città;
    }

    public Regione getRegione() {
        return regione;
    }

    public ArrayList<Casello> getCaselli() {
        return caselli;
    }

    public void setCaselli(ArrayList<Casello> caselli) {
        this.caselli = caselli;
    }
}

