package com.uniupo.biglietto.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "biglietto")
public class Biglietto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_biglietto")
    private Integer idBiglietto;
    
    @Column(name = "id_totem")
    private Integer idTotem;
    
    @Column(name = "targa")
    private String targa;
    
    @Column(name = "timestamp_in")
    private LocalDateTime timestampIn;
    
    @Column(name = "casello_in")
    private Integer caselloIn;

    public Biglietto() {}

    public Biglietto(Integer idTotem, String targa, LocalDateTime timestampIn, Integer caselloIn) {
        this.idTotem = idTotem;
        this.targa = targa;
        this.timestampIn = timestampIn;
        this.caselloIn = caselloIn;
    }

    public Biglietto(Integer idBiglietto, Integer idTotem, String targa, LocalDateTime timestampIn, Integer caselloIn) {
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

    public LocalDateTime getTimestampIn() { return timestampIn; }
    public void setTimestampIn(LocalDateTime timestampIn) { this.timestampIn = timestampIn; }

    public Integer getCaselloIn() { return caselloIn; }
    public void setCaselloIn(Integer caselloIn) { this.caselloIn = caselloIn; }
}
