package com.uniupo.shared.mqtt.dto;


public class BigliettoGeneratoEvent {

    private Integer idCasello;
    private Integer idCorsia;

    public BigliettoGeneratoEvent(Integer idCasello, Integer idCorsia) {
        this.idCasello = idCasello;
        this.idCorsia = idCorsia;
    }

    public Integer getIdCasello() {
        return idCasello;
    }

    public void setIdCasello(Integer idCasello) {
        this.idCasello = idCasello;
    }

    public Integer getIdCorsia() {
        return idCorsia;
    }

    public void setIdCorsia(Integer idCorsia) {
        this.idCorsia = idCorsia;
    }
}