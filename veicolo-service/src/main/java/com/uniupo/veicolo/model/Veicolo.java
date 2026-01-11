package com.uniupo.veicolo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "veicolo")
public class Veicolo {

    @Id
    @Column(name = "targa")
    private String targa;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "classe_veicolo")
    private TipoVeicolo tipoVeicolo;

    public enum TipoVeicolo { A, B, C, D, E }

    public Veicolo() {}

    public Veicolo(String targa) {
        this.targa = targa;
        this.tipoVeicolo = TipoVeicolo.B; // Default
    }

    public Veicolo(String targa, TipoVeicolo tipoVeicolo) {
        this.targa = targa;
        this.tipoVeicolo = tipoVeicolo;
    }

    // Getters and Setters
    public String getTarga() { return targa; }
    public void setTarga(String targa) { this.targa = targa; }

    public TipoVeicolo getTipoVeicolo() { return tipoVeicolo; }
    public void setTipoVeicolo(TipoVeicolo tipoVeicolo) { this.tipoVeicolo = tipoVeicolo; }
}
