package com.uniupo.shared.mqtt.dto;

public class ElaboraDistanzaEvent {

    private String citta_in;
    private String citta_out;

    public ElaboraDistanzaEvent(String citta_in, String citta_out) {
        this.citta_in = citta_in;
        this.citta_out = citta_out;
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
}
