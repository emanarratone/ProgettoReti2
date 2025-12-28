package com.uniupo.shared.mqtt.dto;

public class TrovaAutoEvent {
    String targa;
    Integer Casello_in;
    Integer Casello_out;

    public TrovaAutoEvent(String targa, Integer casello_in, Integer casello_out) {
        this.targa = targa;
        Casello_in = casello_in;
        Casello_out = casello_out;
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
}
