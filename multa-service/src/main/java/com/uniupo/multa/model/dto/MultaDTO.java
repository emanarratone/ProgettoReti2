package com.uniupo.multa.model.dto;

import com.uniupo.multa.model.Multa;

public class MultaDTO {

    private Long id;
    private String targa;
    private Double importo;
    private String stato;
    private String timestampOut;
    private Long idBiglietto;

    public MultaDTO() {}

    public MultaDTO(Long id, String targa, Double importo, String stato, String timestampOut, Long idBiglietto) {
        this.id = id;
        this.targa = targa;
        this.importo = importo;
        this.stato = stato;
        this.timestampOut = timestampOut;
        this.idBiglietto = idBiglietto;
    }

    public MultaDTO(Multa f, String statoDefault) {
        this.id = f.getId() != null ? f.getId().longValue() : null;
        this.targa = f.getTarga();
        this.importo = f.getImporto();
        this.idBiglietto = f.getIdBiglietto() != null ? f.getIdBiglietto().longValue() : null;
        this.stato = (f.getPagato() != null && f.getPagato()) ? "PAGATO" : statoDefault;
        this.timestampOut = "N/D (Servizio Offline)";
    }

    // Getters
    public Long getId() { return id; }
    public String getTarga() { return targa; }
    public Double getImporto() { return importo; }
    public String getStato() { return stato; }
    public String getTimestampOut() { return timestampOut; }
    public Long getIdBiglietto() { return idBiglietto; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTarga(String targa) { this.targa = targa; }
    public void setImporto(Double importo) { this.importo = importo; }
    public void setStato(String stato) { this.stato = stato; }
    public void setTimestampOut(String timestampOut) { this.timestampOut = timestampOut; }
    public void setIdBiglietto(Long idBiglietto) { this.idBiglietto = idBiglietto; }
}