package model.Autostrada;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Autostrada {

    private Integer id;       // id_autostrada
    private String citta;     // nome autostrada/città
    private Integer idRegione;

    public Autostrada(String citta, Integer idRegione) {
        this.citta = citta;
        this.idRegione = idRegione;
    }

    public Autostrada(Integer id, String citta, Integer idRegione) {
        this.id = id;
        this.citta = citta;
        this.idRegione = idRegione;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCittà() { return citta; }
    public void setCittà(String citta) { this.citta = citta; }

    public Integer getIdRegione() { return idRegione; }
    public void setIdRegione(Integer idRegione) { this.idRegione = idRegione; }
}
