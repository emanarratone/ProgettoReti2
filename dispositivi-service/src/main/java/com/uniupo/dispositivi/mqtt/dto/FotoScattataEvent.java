package com.uniupo.dispositivi.mqtt.dto;

/**
 * Evento pubblicato dalla telecamera dopo aver scattato la foto
 */
public class FotoScattataEvent {
    private Integer idTelecamera;
    private Integer idTotem;
    private Integer idCorsia;
    private Integer idCasello;
    private String targa;
    private String timestamp;
    private String urlFoto;

    public FotoScattataEvent() {}

    public FotoScattataEvent(Integer idTelecamera, Integer idTotem, Integer idCorsia, Integer idCasello,
                           String targa, String timestamp, String urlFoto) {
        this.idTelecamera = idTelecamera;
        this.idCorsia = idCorsia;
        this.idCasello = idCasello;
        this.targa = targa;
        this.timestamp = timestamp;
        this.urlFoto = urlFoto;
    }

    public Integer getIdTelecamera() {
        return idTelecamera;
    }

    public void setIdTelecamera(Integer idTelecamera) {
        this.idTelecamera = idTelecamera;
    }

    public Integer getIdTotem() {
        return idTotem;
    }

    public void setIdTotem(Integer idTotem) {
        this.idTotem = idTotem;
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

    public String getTarga() {
        return targa;
    }

    public void setTarga(String targa) {
        this.targa = targa;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }
}
