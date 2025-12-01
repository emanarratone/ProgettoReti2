package model.Dispositivi;

import model.Autostrada.Corsia.Verso;

public abstract class Dispositivi {
    private final Integer corsia;
    protected final Integer ID;
    protected Boolean status; //guasto = 0 o funzionante = 1

    public Dispositivi(Integer ID, Boolean status, Integer corsia) {
        this.ID = ID;
        this.status = status;
        this.corsia = corsia;
    }

    public Integer getCorsia() {
        return corsia;
    }

    public Integer getID() { return ID; }

    public String getStatus() {
        return (status)? "ATTIVO" : "INATTIVO";
    }

    public void setStatus(Boolean status) {this.status = status;}

}
