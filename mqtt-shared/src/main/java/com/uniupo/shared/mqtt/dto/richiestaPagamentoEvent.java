package com.uniupo.shared.mqtt.dto;

public class richiestaPagamentoEvent {

    private Integer idBiglietto;
    private Integer caselloOut;

    public richiestaPagamentoEvent(Integer idBiglietto, Integer caselloOut) {
        this.idBiglietto = idBiglietto;
        this.caselloOut = caselloOut;
    }

    public Integer getIdBiglietto() {
        return idBiglietto;
    }

    public void setIdBiglietto(Integer idBiglietto) {
        this.idBiglietto = idBiglietto;
    }

    public Integer getCaselloOut() {
        return caselloOut;
    }

    public void setCaselloOut(Integer caselloOut) {
        this.caselloOut = caselloOut;
    }
}
