package com.uniupo.corsia.model.dto;

import com.uniupo.corsia.model.Corsia;

public class CorsiaDTO {

    private Integer casello;
    private Integer numCorsia;
    private Corsia.Verso verso;
    private Corsia.Tipo tipo;
    private Boolean isClosed;

    public CorsiaDTO() {}

    public CorsiaDTO(Integer casello, Integer numCorsia, Corsia.Verso verso, Corsia.Tipo tipo) {
        this.casello = casello;
        this.numCorsia = numCorsia;
        this.verso = verso;
        this.tipo = tipo;
        this.isClosed = false;
    }

    public CorsiaDTO(Integer casello, Integer numCorsia, Corsia.Verso verso, Corsia.Tipo tipo, boolean isClosed) {
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
