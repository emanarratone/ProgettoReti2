package com.uniupo.biglietto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.Instant;

@Entity
@Table(name = "biglietto")
public class Biglietto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_biglietto")
    @JsonProperty("id_biglietto")
    private Integer idBiglietto;
    
    @Column(name = "id_totem")
    private Integer idTotem;
    
    @Column(name = "targa")
    private String targa;
    
    @Column(name = "timestamp_in")
    private Timestamp timestampIn;
    
    @Column(name = "casello_in")
    private Integer caselloIn;

    public Biglietto() {}

    public Biglietto(Integer idTotem, String targa, Timestamp timestampIn, Integer caselloIn) {
        this.idTotem = idTotem;
        this.targa = targa;
        this.timestampIn = timestampIn;
        this.caselloIn = caselloIn;
    }

    public Biglietto(Integer idBiglietto, Integer idTotem, String targa, Timestamp timestampIn, Integer caselloIn) {
        this.idBiglietto = idBiglietto;
        this.idTotem = idTotem;
        this.targa = targa;
        this.timestampIn = timestampIn;
        this.caselloIn = caselloIn;
    }

    // Getters and Setters
    public Integer getIdBiglietto() { return idBiglietto; }
    public void setIdBiglietto(Integer idBiglietto) { this.idBiglietto = idBiglietto; }

    public Integer getIdTotem() { return idTotem; }
    public void setIdTotem(Integer idTotem) { this.idTotem = idTotem; }

    public String getTarga() { return targa; }
    public void setTarga(String targa) { this.targa = targa; }

    public Timestamp getTimestampIn() { return timestampIn; }
    public void setTimestampIn(Timestamp timestampIn) { this.timestampIn = timestampIn; }

    public Integer getCaselloIn() { return caselloIn; }
    public void setCaselloIn(Integer caselloIn) { this.caselloIn = caselloIn; }
}
