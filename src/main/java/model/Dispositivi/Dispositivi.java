package model.Dispositivi;

public abstract class Dispositivi {

    protected String ID;
    protected Boolean status; //guasto = 0 o funzionante = 1
    protected Tipo tipo;
    public enum Tipo { ENTRATA, USCITA }

    public Dispositivi(String ID, Boolean status, Tipo tipo) {
        this.ID = ID;
        this.status = status;
        this.tipo = tipo;
    }

    public String getID() { return ID; }

    public void setID(String ID) {this.ID = ID;}

    public Boolean getStatus() {return status;}

    public void setStatus(Boolean status) {this.status = status;}

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }
}
