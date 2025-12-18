package com.uniupo.dispositivi.mqtt.dto;

/**
 * Evento pubblicato quando un utente richiede la generazione di un biglietto
 */
public class RichiestaBigliettoEvent {
    private Integer idCorsia;
    private Integer idCasello;
    private String timestamp;

    public RichiestaBigliettoEvent() {}

    public RichiestaBigliettoEvent(Integer idCorsia, Integer idCasello, String timestamp) {
        this.idCorsia = idCorsia;
        this.idCasello = idCasello;
        this.timestamp = timestamp;
    }

    public Integer getIdCorsia() {
        return idCorsia;
    }

    public void setIdCorsia(Integer idCorsia) {
        this.idCorsia = idCorsia;
    }

    public Integer getIdCasello() {
        return idCasello;
    }

    public void setIdCasello(Integer idCasello) {
        this.idCasello = idCasello;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
