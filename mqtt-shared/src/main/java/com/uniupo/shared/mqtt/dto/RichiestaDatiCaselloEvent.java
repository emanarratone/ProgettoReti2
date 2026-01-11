package com.uniupo.shared.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RichiestaDatiCaselloEvent {
    private String comando;

    @JsonProperty("id_casello") // Nome nel database/Java
    @JsonAlias("id")            // Accetta anche "id" inviato dall'ESP32
    private Integer id_casello;

    public RichiestaDatiCaselloEvent() {} // Costruttore vuoto necessario per Jackson

    // Getter e Setter
    public String getComando() { return comando; }
    public void setComando(String comando) { this.comando = comando; }
    public Integer getIdCasello() { return id_casello; }
    public void setId_casello(Integer id_casello) { this.id_casello = id_casello; }
}