package model.Dispositivi;

import model.Autostrada.Casello;
import model.Autostrada.Corsia;
import model.Autostrada.Corsia.Verso;

public abstract class Dispositivi {
    private final Corsia corsia;
    private final Casello casello;
    protected final Integer ID;
    protected Boolean status; //guasto = 0 o funzionante = 1

    public Dispositivi(Integer ID, Boolean status, Corsia corsia,  Casello casello) {
        this.ID = ID;
        this.status = status;
        this.corsia = corsia;
        this.casello = casello;
    }

    public Corsia getCorsia() {
        return corsia;
    }

    public Integer getID() { return ID; }

    public Casello getCasello() { return casello; }

    public String getStatus() {
        return (status)? "ATTIVO" : "INATTIVO";
    }

    public void setStatus(Boolean status) {this.status = status;}

}
