package corsia_service.model;

import model.Autostrada.Corsia;

public class corsiaDTO {

    private  Integer casello;
    private  Integer numCorsia;
    private Corsia.Verso verso;
    private Corsia.Tipo tipo;
    public enum Tipo {MANUALE, TELEPASS}
    public enum Verso { ENTRATA, USCITA }
    private Boolean isClosed;

    public corsiaDTO(Integer casello, Integer numCorsia, Corsia.Verso verso, Corsia.Tipo tipo) {
        this.casello = casello;
        this.numCorsia = numCorsia;
        this.verso = verso;
        this.tipo = tipo;
        this.isClosed = false;
    }

    public corsiaDTO(Integer casello, Integer numCorsia, Corsia.Verso verso, Corsia.Tipo tipo, boolean isClosed) {
        this.casello = casello;
        this.numCorsia = numCorsia;
        this.verso = verso;
        this.tipo = tipo;
        this.isClosed = isClosed;
    }

    public Corsia.Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Corsia.Tipo tipo) {
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

    public Corsia.Verso getVerso() {
        return verso;
    }

    public void setVerso(Corsia.Verso verso) {
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
