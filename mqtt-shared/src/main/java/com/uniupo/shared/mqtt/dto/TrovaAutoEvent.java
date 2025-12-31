package com.uniupo.shared.mqtt.dto;

public class TrovaAutoEvent {
    private String targa;
    private Integer Casello_in;
    private Integer Casello_out;
    private Integer idBiglietto;
    private Integer corsia;

    public TrovaAutoEvent(String targa, Integer casello_in, Integer casello_out, Integer idBiglietto, Integer corsia) {
        this.targa = targa;
        this.Casello_in = casello_in;
        this.Casello_out = casello_out;
        this.corsia = corsia;
        this.idBiglietto = idBiglietto;
    }

    public Integer getCorsia() {
        return corsia;
    }

    public void setCorsia(Integer corsia) {
        this.corsia = corsia;
    }

    public String getTarga() {
        return targa;
    }

    public void setTarga(String targa) {
        this.targa = targa;
    }

    public Integer getCasello_in() {
        return Casello_in;
    }

    public void setCasello_in(Integer casello_in) {
        Casello_in = casello_in;
    }

    public Integer getCasello_out() {
        return Casello_out;
    }

    public void setCasello_out(Integer casello_out) {
        Casello_out = casello_out;
    }

    public Integer getIdBiglietto() {
        return idBiglietto;
    }

    public void setIdBiglietto(Integer idBiglietto) {
        this.idBiglietto = idBiglietto;
    }
}
