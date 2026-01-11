package com.uniupo.shared.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RichiestaDatiCorsiaEvent {
    private String comando;

    @JsonProperty("id_casello")
    private Integer idCasello;

    @JsonProperty("num_corsia")
    private Integer numCorsia;

    public RichiestaDatiCorsiaEvent() {}


    public String getComando() {return comando;}

    public void setComando(String comando) {this.comando = comando;}

    public Integer getIdCasello() {return idCasello;}

    public void setIdCasello(Integer idCasello) {this.idCasello = idCasello;}

    public Integer getNumCorsia() {return numCorsia;}

    public void setNumCorsia(Integer numCorsia) {this.numCorsia = numCorsia;}
}