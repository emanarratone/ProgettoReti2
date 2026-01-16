package com.uniupo.shared.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class richiestaPagamentoEvent {
    @JsonProperty("idBiglietto")
    private Integer idBiglietto;
    @JsonProperty("caselloOut")
    private Integer caselloOut;

    public String getTarga() {
        return targa;
    }

    private String targa;
    private Integer corsia;
    private boolean isTelepass;
    public richiestaPagamentoEvent(Integer idBiglietto, Integer caselloOut, Integer corsia) {
        this.idBiglietto = idBiglietto;
        this.caselloOut = caselloOut;
        this.corsia = corsia;
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

    public Integer getCorsia() {
        return corsia;
    }

    public void setCorsia(Integer corsia) {
        this.corsia = corsia;
    }

}
