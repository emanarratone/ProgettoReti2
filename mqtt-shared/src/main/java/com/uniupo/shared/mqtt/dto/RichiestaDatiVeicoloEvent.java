package com.uniupo.shared.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RichiestaDatiVeicoloEvent {
    private String comando;

    @JsonProperty("targa")
    private String targa;

    @JsonProperty("classe_veicolo")
    private Integer classe_veicolo;

    public RichiestaDatiVeicoloEvent() {}

    public Integer getClasse_veicolo() {
        return classe_veicolo;
    }

    public String getTarga() {return targa;}
}
