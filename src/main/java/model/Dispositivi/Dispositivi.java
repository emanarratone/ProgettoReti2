package model.Dispositivi;

import model.Autostrada.Corsia.Tipo;

public abstract class Dispositivi {
    private final Integer corsia;
    protected final Integer ID;
    protected Boolean status; //guasto = 0 o funzionante = 1
    protected Tipo tipo;

    public Dispositivi(Integer ID, Boolean status, Tipo tipo, Integer corsia) {
        this.ID = ID;
        this.status = status;
        this.tipo = tipo;
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

    public String getTipo() {
        return tipo.toString();
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }
}
