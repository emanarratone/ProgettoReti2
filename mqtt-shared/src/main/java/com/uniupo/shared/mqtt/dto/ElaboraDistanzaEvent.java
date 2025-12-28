package com.uniupo.shared.mqtt.dto;

public class ElaboraDistanzaEvent {

    private String citta_in;
    private String citta_out;
    private String Classe_veicolo;
    private Integer idBiglietto;
    private Integer casello_out;

    public ElaboraDistanzaEvent(String citta_in, String citta_out, String classe_veicolo, Integer idBiglietto, Integer casello_out) {
        this.citta_in = citta_in;
        this.citta_out = citta_out;
        Classe_veicolo = classe_veicolo;
        this.idBiglietto = idBiglietto;
        this.casello_out = casello_out;
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

    public Integer getIdBiglietto() {
        return idBiglietto;
    }

    public void setIdBiglietto(Integer idBiglietto) {
        this.idBiglietto = idBiglietto;
    }

    public Integer getCasello_out() {
        return casello_out;
    }

    public void setCasello_out(Integer casello_out) {
        this.casello_out = casello_out;
    }
}
