package com.uniupo.shared.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RichiestaDatiDispositiviEvent {
    private String comando;

    @JsonProperty("id_casello")
    private Integer idCasello;

    @JsonProperty("num_corsia")
    private Integer num_corsia;

    public RichiestaDatiDispositiviEvent() {}

    public Integer getidCasello() {return idCasello;}

    public Integer getNum_corsia() {return num_corsia;}

    public String getComando() {return comando;}

    public void setComando(String comando) {this.comando = comando;}

}
