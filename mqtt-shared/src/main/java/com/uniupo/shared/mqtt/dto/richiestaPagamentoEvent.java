package com.uniupo.shared.mqtt.dto;

public class richiestaPagamentoEvent {

    private Integer idBiglietto;
    private Integer caselloOut;
    private Integer corsia;

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
