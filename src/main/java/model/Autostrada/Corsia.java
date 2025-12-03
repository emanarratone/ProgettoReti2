package model.Autostrada;


public class Corsia {

    private final Casello casello;
    private final Integer numCorsia;
    private Verso verso;
    private Tipo tipo;
    public enum Tipo {MANUALE, TELEPASS}
    public enum Verso { ENTRATA, USCITA }
    private Boolean isClosed;

    public Corsia(Casello casello,Integer ID, Verso verso, Tipo tipo) {
        this.casello = casello;
        this.numCorsia = ID;
        this.verso = verso;
        this.tipo = tipo;
        this.isClosed = false;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Boolean getClosed() {
        return isClosed;
    }

    public void setClosed(Boolean closed) {
        isClosed = closed;
    }

    public Casello getCasello() {
        return casello;
    }

    public Verso getVerso() {
        return verso;
    }

    public void setVerso(Verso verso) {
        this.verso = verso;
    }

    public Integer getNumCorsia() {
        return numCorsia;
    }

    @Override
    public String toString() {
        return "Casello "+ this.casello + "\n" +
                "ID "+ this.numCorsia + "\n" +
                "Stato: " + (this.isClosed ? "aperto" : "chiuso");
    }

}
