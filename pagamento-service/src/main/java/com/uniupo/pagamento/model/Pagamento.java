package com.uniupo.pagamento.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pagamento") // Allineato al nome SQL
    private Integer idPagamento;

    @Column(name = "id_biglietto", nullable = false)
    private Integer idBiglietto;

    @JsonProperty("importo")
    @Column(name = "importo", nullable = false)
    private Double prezzo;

    @Column(name = "stato", nullable = false)
    private String stato; // Cambiato da Boolean a String per contenere 'PAGATO' o 'NON_PAGATO'

    @Column(name = "timestamp_out", nullable = false)
    private LocalDateTime timestampOut;

    @Column(name = "casello_out", nullable = false)
    private Integer caselloOut;

    public Pagamento() {}

    // Costruttore per nuovi inserimenti (senza ID perché autogenerato)
    public Pagamento(Integer idBiglietto, Double prezzo, String stato, LocalDateTime timestampOut, Integer caselloOut) {
        this.idBiglietto = idBiglietto;
        this.prezzo = prezzo;
        this.stato = stato;
        this.timestampOut = timestampOut;
        this.caselloOut = caselloOut;
    }

    // Getters and Setters
    public Integer getIdPagamento() { return idPagamento; }
    public void setIdPagamento(Integer idPagamento) { this.idPagamento = idPagamento; }

    public Integer getIdBiglietto() { return idBiglietto; }
    public void setIdBiglietto(Integer idBiglietto) { this.idBiglietto = idBiglietto; }

    public Integer getCaselloOut() { return caselloOut; }
    public void setCaselloOut(Integer caselloOut) { this.caselloOut = caselloOut; }

    public Double getPrezzo() { return prezzo; }
    public void setPrezzo(Double prezzo) { this.prezzo = prezzo; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public LocalDateTime getTimestampOut() { return timestampOut; }
    public void setTimestampOut(LocalDateTime timestampOut) { this.timestampOut = timestampOut; }
}