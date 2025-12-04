package model.Autostrada;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Autostrada {

    private Integer id;
    @JsonProperty("citta")// ID autostrada
    private String citta;      // nome città / autostrada
    private Integer idRegione; // id regione

    public Autostrada() {
        // necessario per Spring (deserializzazione JSON)
    }

    public Autostrada(Integer id, String citta, Integer idRegione) {
        this.id = id;
        this.citta = citta;
        this.idRegione = idRegione;
    }

    public Integer getID() {
        return id;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public String getCittà() {
        return citta;
    }

    public void setCittà(String citta) {
        this.citta = citta;
    }

    // nel DAO prima usavi getRegione(): lo manteniamo
    public Integer getRegione() {
        return idRegione;
    }

    public void setRegione(Integer idRegione) {
        this.idRegione = idRegione;
    }

    // getter/setter espliciti per idRegione se vuoi usarli dal controller
    public Integer getIdRegione() {
        return idRegione;
    }

    public void setIdRegione(Integer idRegione) {
        this.idRegione = idRegione;
    }
}
