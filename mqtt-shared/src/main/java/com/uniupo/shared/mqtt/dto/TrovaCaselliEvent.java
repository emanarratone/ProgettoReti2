package com.uniupo.shared.mqtt.dto;

public class TrovaCaselliEvent {

    private Integer casello_in;
    private Integer casello_out;
    private String classe_veicolo;

    public TrovaCaselliEvent(Integer casello_in, Integer casello_out, String classe_veicolo) {
        this.casello_in = casello_in;
        this.casello_out = casello_out;
        this.classe_veicolo = classe_veicolo;
    }

    public Integer getCasello_in() {
        return casello_in;
    }

    public void setCasello_in(Integer casello_in) {
        this.casello_in = casello_in;
    }

    public Integer getCasello_out() {
        return casello_out;
    }

    public void setCasello_out(Integer casello_out) {
        this.casello_out = casello_out;
    }

    public String getClasse_veicolo() {
        return classe_veicolo;
    }

    public void setClasse_veicolo(String classe_veicolo) {
        this.classe_veicolo = classe_veicolo;
    }
}
