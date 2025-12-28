package com.uniupo.shared.mqtt.dto;

public class ElaboraDistanzaEvent {

    private String citta_in;
    private String citta_out;
    private String Classe_veicolo;

    public ElaboraDistanzaEvent(String citta_in, String citta_out, String classe_veicolo) {
        this.citta_in = citta_in;
        this.citta_out = citta_out;
        Classe_veicolo = classe_veicolo;
    }

    public String getCitta_in() {
        return citta_in;
    }

    public void setCitta_in(String citta_in) {
        this.citta_in = citta_in;
    }

    public String getCitta_out() {
        return citta_out;
    }

    public void setCitta_out(String citta_out) {
        this.citta_out = citta_out;
    }

    public String getClasse_veicolo() {
        return Classe_veicolo;
    }

    public void setClasse_veicolo(String classe_veicolo) {
        Classe_veicolo = classe_veicolo;
    }
}
