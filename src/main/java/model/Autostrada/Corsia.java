package model.Autostrada;


public class Corsia {

    private  Integer casello;
    private  Integer numCorsia;
    private Verso verso;
    private Tipo tipo;

    public Corsia() {}

    public enum Tipo {MANUALE, TELEPASS}
    public enum Verso { ENTRATA, USCITA }
    private Boolean isClosed;

    public Corsia(Integer casello,Integer numCorsia, Verso verso, Tipo tipo) {
        this.casello = casello;
        this.numCorsia = numCorsia;
        this.verso = verso;
        this.tipo = tipo;
        this.isClosed = false;
    }

    public Corsia(Integer casello,Integer numCorsia, Verso verso, Tipo tipo, boolean isClosed) {
        this.casello = casello;
        this.numCorsia = numCorsia;
        this.verso = verso;
        this.tipo = tipo;
        this.isClosed = isClosed;
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

    public Integer getCasello() {
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
