package model.Dispositivi;

import model.Autostrada.Casello;
import model.Autostrada.Corsia;
import model.Autostrada.Corsia.Verso;

public abstract class Dispositivi {
    private final Integer corsia;
    private final Integer casello;
    protected Integer ID;
    protected Boolean status; //guasto = 0 o funzionante = 1

    public Dispositivi(Integer ID, Boolean status, Integer corsia,  Integer casello) {
        this.ID = ID;
        this.status = status;
        this.corsia = corsia;
        this.casello = casello;
    }

    public Dispositivi(Boolean status, Integer corsia,  Integer casello) {
        this.status = status;
        this.corsia = corsia;
        this.casello = casello;
    }

    public Integer getCorsia() {
        return corsia;
    }

    public Integer getID() { return ID; }

    public Integer getCasello() { return casello; }

    public String getStatus() {
        return (status)? "ATTIVO" : "INATTIVO";
    }

    public void setStatus(Boolean status) {this.status = status;}

}
