package com.uniupo.shared.mqtt.dto;

public class CreazioneMultaEvent {

    private Integer idBiglietto;
    private String targa;

    public CreazioneMultaEvent(Integer idBiglietto, String targa) {
        this.idBiglietto = idBiglietto;
        this.targa = targa;
    }

    public String getTarga() {
        return targa;
    }

    public void setTarga(String targa) {
        this.targa = targa;
    }

    public Integer getIdBiglietto() {
        return idBiglietto;
    }

    public void setIdBiglietto(Integer idBiglietto) {
        this.idBiglietto = idBiglietto;
    }
}
